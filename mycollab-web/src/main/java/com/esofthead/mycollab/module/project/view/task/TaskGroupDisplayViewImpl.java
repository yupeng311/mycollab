/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.view.task;

import com.esofthead.mycollab.common.i18n.FileI18nEnum;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.core.arguments.SetSearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectRolePermissionCollections;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.domain.SimpleTaskList;
import com.esofthead.mycollab.module.project.domain.criteria.TaskListSearchCriteria;
import com.esofthead.mycollab.module.project.domain.criteria.TaskSearchCriteria;
import com.esofthead.mycollab.module.project.events.TaskEvent;
import com.esofthead.mycollab.module.project.events.TaskListEvent;
import com.esofthead.mycollab.module.project.i18n.TaskGroupI18nEnum;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.reporting.ExportTaskListStreamResource;
import com.esofthead.mycollab.module.project.service.ProjectTaskListService;
import com.esofthead.mycollab.module.project.ui.ProjectAssetsManager;
import com.esofthead.mycollab.module.project.view.parameters.TaskFilterParameter;
import com.esofthead.mycollab.reporting.ReportExportType;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.events.*;
import com.esofthead.mycollab.vaadin.mvp.AbstractLazyPageView;
import com.esofthead.mycollab.vaadin.mvp.ViewComponent;
import com.esofthead.mycollab.vaadin.mvp.ViewScope;
import com.esofthead.mycollab.vaadin.ui.ToggleButtonGroup;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.esofthead.mycollab.vaadin.ui.table.AbstractPagedBeanTable;
import com.esofthead.vaadin.floatingcomponent.FloatingComponent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.hene.popupbutton.PopupButton;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * @author MyCollab Ltd.
 * @since 1.0
 */
@ViewComponent(scope = ViewScope.PROTOTYPE)
public class TaskGroupDisplayViewImpl extends AbstractLazyPageView implements
        TaskGroupDisplayView {
    private static final long serialVersionUID = 1L;

    private PopupButton taskGroupSelection;
    private PopupButton taskSelection;
    private TaskGroupDisplayWidget taskListsWidget;

    private VerticalLayout rightColumn;
    private TextField nameField;

    private TaskSearchViewImpl basicSearchView;

    private MHorizontalLayout header;
    private MHorizontalLayout mainLayout;
    private ToggleButtonGroup viewButtons;

    private void implementTaskFilterButton() {
        this.taskSelection = new PopupButton(
                AppContext.getMessage(TaskGroupI18nEnum.FILTER_ACTIVE_TASKS));

        this.taskSelection.setEnabled(CurrentProjectVariables
                .canRead(ProjectRolePermissionCollections.TASKS));
        this.taskSelection.addStyleName("link");
        this.taskSelection.addStyleName("hdr-text");

        final MVerticalLayout filterBtnLayout = new MVerticalLayout().withWidth("200px");

        final Button allTasksFilterBtn = new Button(
                AppContext.getMessage(TaskGroupI18nEnum.FILTER_ALL_TASKS),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        taskSelection.setPopupVisible(false);
                        taskSelection
                                .setCaption(event.getButton().getCaption());
                        displayAllTasks();
                    }
                });
        allTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(allTasksFilterBtn);

        final Button activeTasksFilterBtn = new Button(
                AppContext.getMessage(TaskGroupI18nEnum.FILTER_ACTIVE_TASKS),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        taskSelection.setPopupVisible(false);
                        taskSelection
                                .setCaption(event.getButton().getCaption());
                        displayActiveTasksOnly();
                    }
                });
        activeTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(activeTasksFilterBtn);

        final Button pendingTasksFilterBtn = new Button(
                AppContext.getMessage(TaskGroupI18nEnum.FILTER_PENDING_TASKS),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        taskSelection.setPopupVisible(false);
                        taskSelection
                                .setCaption(event.getButton().getCaption());
                        displayPendingTasksOnly();
                    }
                });
        pendingTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(pendingTasksFilterBtn);

        final Button archievedTasksFilterBtn = new Button(
                AppContext.getMessage(TaskGroupI18nEnum.FILTER_ARCHIEVED_TASKS),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        taskSelection.setPopupVisible(false);
                        taskSelection
                                .setCaption(event.getButton().getCaption());
                        displayInActiveTasks();
                    }
                });
        archievedTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(archievedTasksFilterBtn);
        taskSelection.setContent(filterBtnLayout);
    }

    private void constructUI() {
        this.removeAllComponents();
        this.withMargin(new MarginInfo(false, true, true, true));

        header = new MHorizontalLayout()
                .withMargin(new MarginInfo(true, false, true, false))
                .withStyleName("hdr-view").withWidth("100%");
        header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        this.taskGroupSelection = new PopupButton(
                AppContext
                        .getMessage(TaskGroupI18nEnum.FILTER_ACTIVE_TASK_GROUPS_TITLE));
        this.taskGroupSelection.setEnabled(CurrentProjectVariables
                .canRead(ProjectRolePermissionCollections.TASKS));
        this.taskGroupSelection.addStyleName("link");
        this.taskGroupSelection.addStyleName("hdr-text");
        taskGroupSelection.setIcon(ProjectAssetsManager.getAsset(ProjectTypeConstants.TASK_LIST));
        header.with(taskGroupSelection)
                .withAlign(taskGroupSelection, Alignment.MIDDLE_LEFT)
                .expand(taskGroupSelection);

        final MVerticalLayout filterBtnLayout = new MVerticalLayout()
                .withMargin(true).withSpacing(true).withWidth("200px");

        final Button allTasksFilterBtn = new Button(
                AppContext
                        .getMessage(TaskGroupI18nEnum.FILTER_ALL_TASK_GROUPS_TITLE),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        TaskGroupDisplayViewImpl.this.taskGroupSelection
                                .setPopupVisible(false);
                        TaskGroupDisplayViewImpl.this.taskGroupSelection.setCaption(AppContext
                                .getMessage(TaskGroupI18nEnum.FILTER_ALL_TASK_GROUPS_TITLE));
                        TaskGroupDisplayViewImpl.this.displayAllTaskGroups();
                    }
                });
        allTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(allTasksFilterBtn);

        final Button activeTasksFilterBtn = new Button(
                AppContext
                        .getMessage(TaskGroupI18nEnum.FILTER_ACTIVE_TASK_GROUPS_TITLE),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        TaskGroupDisplayViewImpl.this.taskGroupSelection
                                .setPopupVisible(false);
                        TaskGroupDisplayViewImpl.this.taskGroupSelection.setCaption(AppContext
                                .getMessage(TaskGroupI18nEnum.FILTER_ACTIVE_TASK_GROUPS_TITLE));
                        TaskGroupDisplayViewImpl.this.displayActiveTaskGroups();
                    }
                });
        activeTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(activeTasksFilterBtn);

        final Button archivedTasksFilterBtn = new Button(
                AppContext
                        .getMessage(TaskGroupI18nEnum.FILTER_ARCHIEVED_TASK_GROUPS_TITLE),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        TaskGroupDisplayViewImpl.this.taskGroupSelection.setCaption(AppContext
                                .getMessage(TaskGroupI18nEnum.FILTER_ARCHIEVED_TASK_GROUPS_TITLE));
                        TaskGroupDisplayViewImpl.this.taskGroupSelection
                                .setPopupVisible(false);
                        TaskGroupDisplayViewImpl.this
                                .displayInActiveTaskGroups();
                    }
                });
        archivedTasksFilterBtn.setStyleName("link");
        filterBtnLayout.addComponent(archivedTasksFilterBtn);

        this.taskGroupSelection.setContent(filterBtnLayout);

        final Button newTaskListBtn = new Button(
                AppContext.getMessage(TaskI18nEnum.BUTTON_NEW_TASKGROUP),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final TaskGroupAddWindow taskListWindow = new TaskGroupAddWindow(
                                TaskGroupDisplayViewImpl.this);
                        UI.getCurrent().addWindow(taskListWindow);
                    }
                });
        newTaskListBtn.setEnabled(CurrentProjectVariables
                .canWrite(ProjectRolePermissionCollections.TASKS));
        newTaskListBtn.setIcon(FontAwesome.PLUS);
        newTaskListBtn.setDescription(AppContext
                .getMessage(TaskI18nEnum.BUTTON_NEW_TASKGROUP));
        newTaskListBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
        header.addComponent(newTaskListBtn);
        header.setComponentAlignment(newTaskListBtn, Alignment.MIDDLE_RIGHT);

        Button reOrderBtn = new Button(null, new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                EventBusFactory.getInstance().post(
                        new TaskListEvent.ReoderTaskList(this, null));
            }
        });
        reOrderBtn.setEnabled(CurrentProjectVariables
                .canWrite(ProjectRolePermissionCollections.TASKS));
        reOrderBtn.setIcon(FontAwesome.SORT);
        reOrderBtn.setStyleName(UIConstants.THEME_BLUE_LINK);
        reOrderBtn.setDescription(AppContext
                .getMessage(TaskI18nEnum.BUTTON_REODER_TASKGROUP));
        header.addComponent(reOrderBtn);
        header.setComponentAlignment(reOrderBtn, Alignment.MIDDLE_RIGHT);

        PopupButton exportButtonControl = new PopupButton();
        exportButtonControl.addStyleName(UIConstants.THEME_BLUE_LINK);
        exportButtonControl.setIcon(FontAwesome.EXTERNAL_LINK);
        exportButtonControl.setDescription("Export to file");

        VerticalLayout popupButtonsControl = new VerticalLayout();
        exportButtonControl.setContent(popupButtonsControl);
        exportButtonControl.setWidthUndefined();

        Button exportPdfBtn = new Button(
                AppContext.getMessage(FileI18nEnum.PDF));
        FileDownloader pdfDownloader = new FileDownloader(
                constructStreamResource(ReportExportType.PDF));
        pdfDownloader.extend(exportPdfBtn);
        exportPdfBtn.setIcon(FontAwesome.FILE_PDF_O);
        exportPdfBtn.setStyleName("link");
        popupButtonsControl.addComponent(exportPdfBtn);

        Button exportExcelBtn = new Button(
                AppContext.getMessage(FileI18nEnum.EXCEL));
        FileDownloader excelDownloader = new FileDownloader(
                constructStreamResource(ReportExportType.EXCEL));
        excelDownloader.extend(exportExcelBtn);
        exportExcelBtn.setIcon(FontAwesome.FILE_EXCEL_O);
        exportExcelBtn.setStyleName("link");
        popupButtonsControl.addComponent(exportExcelBtn);

        header.with(exportButtonControl).withAlign(exportButtonControl, Alignment.MIDDLE_LEFT);

        Button advanceDisplayBtn = new Button(null, new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                displayAdvancedView();
            }
        });
        advanceDisplayBtn.setIcon(FontAwesome.SITEMAP);
        advanceDisplayBtn.setDescription(AppContext
                .getMessage(TaskGroupI18nEnum.ADVANCED_VIEW_TOOLTIP));

        Button simpleDisplayBtn = new Button(null, new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
                searchCriteria.setProjectid(new NumberSearchField(
                        CurrentProjectVariables.getProjectId()));
                searchCriteria.setStatuses(new SetSearchField<>(new String[]{StatusI18nEnum.Open.name()}));
                TaskFilterParameter taskFilter = new TaskFilterParameter(
                        searchCriteria, "Task Search");
                taskFilter.setAdvanceSearch(true);
                moveToTaskSearch(taskFilter);
            }
        });
        simpleDisplayBtn.setIcon(FontAwesome.LIST_UL);
        simpleDisplayBtn.setDescription(AppContext
                .getMessage(TaskGroupI18nEnum.LIST_VIEW_TOOLTIP));

        Button chartDisplayBtn = new Button(null, new Button.ClickListener() {
            private static final long serialVersionUID = -5707546605789537298L;

            @Override
            public void buttonClick(ClickEvent event) {
                displayGanttChartView();
            }
        });
        chartDisplayBtn.setIcon(FontAwesome.BAR_CHART_O);

        viewButtons = new ToggleButtonGroup();
        viewButtons.addButton(simpleDisplayBtn);
        viewButtons.addButton(advanceDisplayBtn);
        viewButtons.addButton(chartDisplayBtn);
        viewButtons.setDefaultButton(advanceDisplayBtn);

        mainLayout = new MHorizontalLayout().withFullHeight().withFullWidth()
                .withSpacing(true);
        this.taskListsWidget = new TaskGroupDisplayWidget();

        MVerticalLayout leftColumn = new MVerticalLayout().withMargin(
                new MarginInfo(false, true, false, false)).with(taskListsWidget);

        this.rightColumn = new MVerticalLayout().withWidth("300px").withMargin(
                new MarginInfo(true, false, false, false));

        mainLayout.with(leftColumn, rightColumn).expand(leftColumn);

        FloatingComponent floatSidebar = FloatingComponent
                .floatThis(this.rightColumn);
        floatSidebar.setContainerId("main-body");

        implementTaskFilterButton();
        basicSearchView = new TaskSearchViewImpl();
        basicSearchView.getSearchHandlers().addSearchHandler(
                new SearchHandler<TaskSearchCriteria>() {
                    @Override
                    public void onSearch(TaskSearchCriteria criteria) {
                        doSearch(criteria);
                    }
                });
        basicSearchView.removeComponent(basicSearchView.getComponent(0));

        displayAdvancedView();
    }

    private void doSearch(TaskSearchCriteria searchCriteria) {
        basicSearchView.getPagedBeanTable().setSearchCriteria(searchCriteria);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private StreamResource constructStreamResource(ReportExportType exportType) {
        final String title = "Tasks report of project "
                + ((CurrentProjectVariables.getProject() != null && CurrentProjectVariables
                .getProject().getName() != null) ? CurrentProjectVariables
                .getProject().getName() : "");

        final TaskListSearchCriteria tasklistSearchCriteria = new TaskListSearchCriteria();
        tasklistSearchCriteria.setProjectId(new NumberSearchField(
                SearchField.AND, CurrentProjectVariables.getProject().getId()));

        StreamResource res;
        if (exportType.equals(ReportExportType.PDF)) {
            res = new StreamResource(new ExportTaskListStreamResource(title,
                    exportType,
                    ApplicationContextUtil
                            .getSpringBean(ProjectTaskListService.class),
                    tasklistSearchCriteria, null), "task_list.pdf");
        } else if (exportType.equals(ReportExportType.CSV)) {
            res = new StreamResource(new ExportTaskListStreamResource(title,
                    exportType,
                    ApplicationContextUtil
                            .getSpringBean(ProjectTaskListService.class),
                    tasklistSearchCriteria, null), "task_list.csv");
        } else {
            res = new StreamResource(new ExportTaskListStreamResource(title,
                    exportType,
                    ApplicationContextUtil
                            .getSpringBean(ProjectTaskListService.class),
                    tasklistSearchCriteria, null), "task_list.xls");
        }

        return res;
    }

    private TaskListSearchCriteria createBaseSearchCriteria() {
        final TaskListSearchCriteria criteria = new TaskListSearchCriteria();
        criteria.setProjectId(new NumberSearchField(CurrentProjectVariables
                .getProjectId()));
        return criteria;
    }

    @Override
    protected void displayView() {
        constructUI();
        displayActiveTaskGroups();
        displayTaskStatistic();
    }

    private VerticalLayout createSearchPanel() {
        MVerticalLayout basicSearchBody = new MVerticalLayout().withMargin(new MarginInfo(true, false, true, false));
        basicSearchBody.addStyleName(UIConstants.BORDER_BOX_2);

        nameField = new TextField();

        nameField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
        basicSearchBody.with(nameField).withAlign(nameField,
                Alignment.MIDDLE_CENTER);

        MHorizontalLayout control = new MHorizontalLayout().withMargin(new MarginInfo(true, false, true, false));

        final Button searchBtn = new Button(
                AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
        searchBtn.setIcon(FontAwesome.SEARCH);
        searchBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
        searchBtn.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
                searchCriteria.setProjectid(new NumberSearchField(
                        CurrentProjectVariables.getProjectId()));
                searchCriteria.setTaskName(new StringSearchField(nameField
                        .getValue().trim()));
                TaskFilterParameter taskFilter = new TaskFilterParameter(
                        searchCriteria, "Task Search");
                moveToTaskSearch(taskFilter);
            }
        });
        control.with(searchBtn).withAlign(searchBtn, Alignment.MIDDLE_CENTER);

        final Button advancedSearchBtn = new Button(
                AppContext.getMessage(GenericI18Enum.BUTTON_ADVANCED_SEARCH),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
                        searchCriteria.setProjectid(new NumberSearchField(
                                CurrentProjectVariables.getProjectId()));
                        searchCriteria.setTaskName(new StringSearchField(
                                nameField.getValue().trim()));
                        TaskFilterParameter taskFilter = new TaskFilterParameter(
                                searchCriteria, "Task Search");
                        taskFilter.setAdvanceSearch(true);
                        moveToTaskSearch(taskFilter);
                    }
                });
        advancedSearchBtn.setStyleName(UIConstants.THEME_BLUE_LINK);
        control.with(advancedSearchBtn).withAlign(advancedSearchBtn,
                Alignment.MIDDLE_CENTER);
        basicSearchBody.with(control).withAlign(control,
                Alignment.MIDDLE_CENTER);

        return basicSearchBody;
    }

    void moveToTaskSearch(TaskFilterParameter taskFilter) {
        EventBusFactory.getInstance().post(
                new TaskEvent.Search(this, taskFilter));
    }

    private void displayTaskStatistic() {
        rightColumn.removeAllComponents();

        rightColumn.addComponent(createSearchPanel());
        UnresolvedTaskByAssigneeWidget unresolvedTaskByAssigneeWidget = new UnresolvedTaskByAssigneeWidget();
        rightColumn.addComponent(unresolvedTaskByAssigneeWidget);

        TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
        searchCriteria.setProjectid(new NumberSearchField(
                CurrentProjectVariables.getProjectId()));
        searchCriteria.setStatuses(new SetSearchField<>(SearchField.AND,
                new String[]{StatusI18nEnum.Open.name()}));

        unresolvedTaskByAssigneeWidget.setSearchCriteria(searchCriteria);

        UnresolvedTaskByPriorityWidget unresolvedTaskByPriorityWidget = new UnresolvedTaskByPriorityWidget();
        rightColumn.addComponent(unresolvedTaskByPriorityWidget);
        unresolvedTaskByPriorityWidget.setSearchCriteria(searchCriteria);
    }

    private void displayAdvancedView() {
        this.removeAllComponents();
        header.with(viewButtons).withAlign(viewButtons, Alignment.MIDDLE_RIGHT);
        this.with(header, mainLayout).withAlign(header, Alignment.TOP_RIGHT);
    }

    private void displayGanttChartView() {
        EventBusFactory.getInstance().post(
                new TaskEvent.GotoGanttChart(this, null));
    }

    private void displayActiveTaskGroups() {
        final TaskListSearchCriteria criteria = this.createBaseSearchCriteria();
        criteria.setStatus(new StringSearchField(StatusI18nEnum.Open.name()));
        this.taskListsWidget.setSearchCriteria(criteria);
    }

    private void displayInActiveTaskGroups() {
        final TaskListSearchCriteria criteria = this.createBaseSearchCriteria();
        criteria.setStatus(new StringSearchField(StatusI18nEnum.Closed.name()));
        this.taskListsWidget.setSearchCriteria(criteria);
    }

    private void displayAllTaskGroups() {
        final TaskListSearchCriteria criteria = this.createBaseSearchCriteria();
        this.taskListsWidget.setSearchCriteria(criteria);
    }

    @Override
    public void insertTaskList(final SimpleTaskList taskList) {
        this.taskListsWidget.insetItemOnBottom(taskList);
    }

    private TaskSearchCriteria createTaskBaseSearchCriteria() {
        final TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setProjectid(new NumberSearchField(CurrentProjectVariables
                .getProjectId()));
        return criteria;
    }

    private void displayActiveTasksOnly() {
        final TaskSearchCriteria criteria = this.createTaskBaseSearchCriteria();
        criteria.setStatuses(new SetSearchField<>(SearchField.AND,
                new String[]{StatusI18nEnum.Open.name()}));
        this.doSearch(criteria);
    }

    private void displayPendingTasksOnly() {
        final TaskSearchCriteria criteria = this.createTaskBaseSearchCriteria();
        criteria.setStatuses(new SetSearchField<>(SearchField.AND,
                new String[]{StatusI18nEnum.Pending.name()}));
        this.doSearch(criteria);
    }

    private void displayAllTasks() {
        final TaskSearchCriteria criteria = this.createTaskBaseSearchCriteria();
        this.doSearch(criteria);
    }

    private void displayInActiveTasks() {
        final TaskSearchCriteria criteria = this.createTaskBaseSearchCriteria();
        criteria.setStatuses(new SetSearchField<>(SearchField.AND,
                new String[]{StatusI18nEnum.Closed.name()}));
        this.doSearch(criteria);
    }

    @Override
    public void enableActionControls(int numOfSelectedItem) {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public void disableActionControls() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public HasSearchHandlers<TaskListSearchCriteria> getSearchHandlers() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public HasSelectionOptionHandlers getOptionSelectionHandlers() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public HasMassItemActionHandlers getPopupActionHandlers() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public HasSelectableItemHandlers<SimpleTaskList> getSelectableItemHandlers() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }

    @Override
    public AbstractPagedBeanTable<TaskListSearchCriteria, SimpleTaskList> getPagedBeanTable() {
        throw new UnsupportedOperationException(
                "This view doesn't support this operation");
    }
}
