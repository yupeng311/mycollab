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

package com.esofthead.mycollab.module.user.accountsettings.team.view;

import com.esofthead.mycollab.core.MyCollabException;
import com.esofthead.mycollab.module.user.domain.Role;
import com.esofthead.mycollab.module.user.domain.SimpleRole;
import com.esofthead.mycollab.security.*;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.events.HasPreviewFormHandlers;
import com.esofthead.mycollab.vaadin.mvp.AbstractPageView;
import com.esofthead.mycollab.vaadin.mvp.ViewComponent;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.maddon.layouts.MHorizontalLayout;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
@ViewComponent
public class RoleReadViewImpl extends AbstractPageView implements RoleReadView {

	private static final long serialVersionUID = 1L;

	protected AdvancedPreviewBeanForm<Role> previewForm;
	protected SimpleRole role;
	private PreviewFormControlsGenerator<Role> buttonControls;

	public RoleReadViewImpl() {
		super();
		this.setMargin(new MarginInfo(false, true, false, true));

		MHorizontalLayout header = new MHorizontalLayout().withMargin(new MarginInfo(true, false, true, false))
                .withWidth("100%").withStyleName(UIConstants.HEADER_VIEW);
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		Label headerText = new Label(FontAwesome.USERS.getHtml() + " Detail Role", ContentMode.HTML);
		headerText.setStyleName(UIConstants.HEADER_TEXT);

		header.with(headerText).expand(headerText);

		this.addComponent(header);

		this.previewForm = new AdvancedPreviewBeanForm<>();
		this.addComponent(this.previewForm);

		Layout controlButtons = createTopPanel();
		if (controlButtons != null) {
			header.addComponent(controlButtons);
		}
	}

	protected Layout createTopPanel() {
		buttonControls = new PreviewFormControlsGenerator<>(previewForm);
		return buttonControls
				.createButtonControls(RolePermissionCollections.ACCOUNT_ROLE);
	}

	@Override
	public void previewItem(final SimpleRole role) {
		this.role = role;
		this.previewForm.setFormLayoutFactory(new FormLayoutFactory());
		this.previewForm
				.setBeanFormFieldFactory(new AbstractBeanFieldGroupViewFieldFactory<Role>(
						previewForm) {
					private static final long serialVersionUID = 1L;

					@Override
					protected Field<?> onCreateField(Object propertyId) {
						return null;
					}
				});
		this.previewForm.setBean(role);
		if (role.getIssystemrole() != null
				&& role.getIssystemrole() == Boolean.TRUE) {
			buttonControls.setDeleteButtonVisible(false);
		} else {
			buttonControls.setDeleteButtonVisible(true);
		}
	}

	@Override
	public HasPreviewFormHandlers<Role> getPreviewFormHandlers() {
		return this.previewForm;
	}

	private static String getValueFromPerPath(
			final PermissionMap permissionMap, final String permissionItem) {
		final Integer perVal = permissionMap.get(permissionItem);
		if (perVal == null) {
			return "Undefined";
		} else {
			if (PermissionChecker.isAccessPermission(perVal)) {
				return AppContext
						.getMessage(AccessPermissionFlag.toKey(perVal));
			} else if (PermissionChecker.isBooleanPermission(perVal)) {
				return AppContext.getMessage(BooleanPermissionFlag
						.toKey(perVal));
			} else {
				throw new MyCollabException("Do not support permission value "
						+ perVal);
			}

		}
	}

	protected Depot constructPermissionSectionView(String depotTitle,
			PermissionMap permissionMap, PermissionDefItem[] defItems) {
		final GridFormLayoutHelper formHelper = new GridFormLayoutHelper(2,
				defItems.length, "100%", "167px", Alignment.TOP_LEFT);
		formHelper.getLayout().setMargin(true);
		formHelper.getLayout().setWidth("100%");
		formHelper.getLayout().addStyleName(UIConstants.COLORED_GRIDLAYOUT);
		final Depot component = new Depot(depotTitle, formHelper.getLayout());

		for (int i = 0; i < defItems.length; i++) {
			final PermissionDefItem permissionDefItem = defItems[i];
			formHelper.addComponent(
					new Label(getValueFromPerPath(permissionMap,
							permissionDefItem.getKey())), permissionDefItem
							.getCaption(), 0, i);
		}
		return component;
	}

	@Override
	public SimpleRole getItem() {
		return this.role;
	}

	class FormLayoutFactory extends RoleFormLayoutFactory {

		private static final long serialVersionUID = 1L;

		public FormLayoutFactory() {
			super(RoleReadViewImpl.this.role.getRolename());
		}

		@Override
		protected Layout createBottomPanel() {
			final VerticalLayout permissionsPanel = new VerticalLayout();
			final Label organizationHeader = new Label("Permissions");
			organizationHeader.setStyleName("h2");
			permissionsPanel.addComponent(organizationHeader);

			final PermissionMap permissionMap = RoleReadViewImpl.this.role
					.getPermissionMap();

			permissionsPanel.addComponent(constructPermissionSectionView(
					"Project", permissionMap,
					RolePermissionCollections.PROJECT_PERMISSION_ARR));

			permissionsPanel.addComponent(constructPermissionSectionView(
					"Customer Management", permissionMap,
					RolePermissionCollections.CRM_PERMISSIONS_ARR));

			permissionsPanel.addComponent(constructPermissionSectionView(
					"Document", permissionMap,
					RolePermissionCollections.DOCUMENT_PERMISSION_ARR));

			permissionsPanel.addComponent(constructPermissionSectionView(
					"Account Management", permissionMap,
					RolePermissionCollections.ACCOUNT_PERMISSION_ARR));

			return permissionsPanel;
		}

	}
}
