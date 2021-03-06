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

import com.esofthead.mycollab.common.ModuleNameConstants;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum.TaskPriority;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.ui.format.TaskGroupHistoryFieldFormat;
import com.esofthead.mycollab.module.user.ui.components.UserHistoryFieldFormat;
import com.esofthead.mycollab.utils.FieldGroupFormatter;
import com.esofthead.mycollab.utils.FieldGroupFormatter.I18nHistoryFieldFormat;
import com.esofthead.mycollab.vaadin.ui.HistoryLogComponent;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 */
public class TaskHistoryList extends HistoryLogComponent {
	private static final long serialVersionUID = 1L;

	public static final FieldGroupFormatter taskFormatter;

	static {
		taskFormatter = new FieldGroupFormatter();
		taskFormatter.generateFieldDisplayHandler("taskname",
				TaskI18nEnum.FORM_TASK_NAME);
		taskFormatter.generateFieldDisplayHandler("startdate",
				TaskI18nEnum.FORM_START_DATE, FieldGroupFormatter.DATE_FIELD);
		taskFormatter.generateFieldDisplayHandler("enddate",
				TaskI18nEnum.FORM_END_DATE, FieldGroupFormatter.DATE_FIELD);
		taskFormatter.generateFieldDisplayHandler("actualstartdate",
				TaskI18nEnum.FORM_ACTUAL_START_DATE,
				FieldGroupFormatter.DATE_FIELD);
		taskFormatter.generateFieldDisplayHandler("actualenddate",
				TaskI18nEnum.FORM_ACTUAL_END_DATE,
				FieldGroupFormatter.DATE_FIELD);
		taskFormatter.generateFieldDisplayHandler("deadline",
				TaskI18nEnum.FORM_DEADLINE, FieldGroupFormatter.DATE_FIELD);
		taskFormatter.generateFieldDisplayHandler("priority",
				TaskI18nEnum.FORM_PRIORITY, new I18nHistoryFieldFormat(
						TaskPriority.class));
		taskFormatter.generateFieldDisplayHandler("isestimated",
				TaskI18nEnum.FORM_IS_ESTIMATED);
		taskFormatter.generateFieldDisplayHandler("remainestimate",
				TaskI18nEnum.FORM_REMAIN_ESTIMATE);
		taskFormatter.generateFieldDisplayHandler("assignuser",
				GenericI18Enum.FORM_ASSIGNEE, new UserHistoryFieldFormat());
		taskFormatter.generateFieldDisplayHandler("tasklistid",
				TaskI18nEnum.FORM_TASKGROUP, new TaskGroupHistoryFieldFormat());
		taskFormatter.generateFieldDisplayHandler("percentagecomplete",
				TaskI18nEnum.FORM_PERCENTAGE_COMPLETE);
		taskFormatter.generateFieldDisplayHandler("notes",
				TaskI18nEnum.FORM_NOTES);
	}

	public TaskHistoryList() {
		super(ModuleNameConstants.PRJ, ProjectTypeConstants.TASK);
	}

	@Override
	protected FieldGroupFormatter buildFormatter() {
		return taskFormatter;
	}
}
