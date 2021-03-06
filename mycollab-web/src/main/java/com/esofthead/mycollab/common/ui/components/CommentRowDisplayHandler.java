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

package com.esofthead.mycollab.common.ui.components;

import com.esofthead.mycollab.common.domain.SimpleComment;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.common.service.CommentService;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.core.utils.DateTimeUtils;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.ecm.domain.Content;
import com.esofthead.mycollab.module.project.events.ProjectMemberEvent;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.commons.collections.CollectionUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import java.util.List;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class CommentRowDisplayHandler extends
		BeanList.RowDisplayHandler<SimpleComment> {
	private static final long serialVersionUID = 1L;

	@Override
	public Component generateRow(final SimpleComment comment, int rowIndex) {
		final MHorizontalLayout layout = new MHorizontalLayout().withSpacing(true).withMargin(false).withWidth
				("100%").withStyleName("message");

		MVerticalLayout userBlock = new MVerticalLayout().withSpacing(true).withMargin(false).withWidth("80px");
		userBlock.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		ClickListener gotoUser = new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				EventBusFactory.getInstance().post(
						new ProjectMemberEvent.GotoRead(this, comment
								.getCreateduser()));
			}
		};
		Button userAvatarBtn = UserAvatarControlFactory
				.createUserAvatarButtonLink(comment.getOwnerAvatarId(),
						comment.getOwnerFullName());
		userAvatarBtn.addClickListener(gotoUser);
		userBlock.addComponent(userAvatarBtn);

		Button userName = new Button(comment.getOwnerFullName());
		userName.setStyleName("user-name");
		userName.addStyleName("link");
		userName.addStyleName(UIConstants.WORD_WRAP);
		userName.addClickListener(gotoUser);
		userBlock.addComponent(userName);
		layout.addComponent(userBlock);

		CssLayout rowLayout = new CssLayout();
		rowLayout.setStyleName("message-container");
		rowLayout.setWidth("100%");

		MHorizontalLayout messageHeader = new MHorizontalLayout().withSpacing(true).withMargin(new MarginInfo(true,
				true, false, true)).withWidth("100%").withStyleName("message-header");
		messageHeader.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		Label timePostLbl = new Label(AppContext.getMessage(
				GenericI18Enum.EXT_ADDED_COMMENT, comment.getOwnerFullName(),
				DateTimeUtils.getPrettyDateValue(comment.getCreatedtime(),
						AppContext.getUserLocale())), ContentMode.HTML);
		timePostLbl.setDescription(AppContext.formatDateTime(comment
				.getCreatedtime()));

		timePostLbl.setSizeUndefined();
		timePostLbl.setStyleName("time-post");
		messageHeader.addComponent(timePostLbl);
		messageHeader.setExpandRatio(timePostLbl, 1.0f);

		// Message delete button
		Button msgDeleteBtn = new Button();
		msgDeleteBtn.setIcon(FontAwesome.TRASH_O);
		msgDeleteBtn.setStyleName(UIConstants.BUTTON_ICON_ONLY);
		messageHeader.addComponent(msgDeleteBtn);

		if (hasDeletePermission(comment)) {
			msgDeleteBtn.setVisible(true);
			msgDeleteBtn.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					ConfirmDialogExt.show(
							UI.getCurrent(),
							AppContext.getMessage(
									GenericI18Enum.DIALOG_DELETE_TITLE,
									SiteConfiguration.getSiteName()),
							AppContext
									.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
							AppContext.getMessage(GenericI18Enum.BUTTON_YES),
							AppContext.getMessage(GenericI18Enum.BUTTON_NO),
							new ConfirmDialog.Listener() {
								private static final long serialVersionUID = 1L;

								@Override
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										CommentService commentService = ApplicationContextUtil
												.getSpringBean(CommentService.class);
										commentService.removeWithSession(
												comment.getId(),
												AppContext.getUsername(),
												AppContext.getAccountId());
										CommentRowDisplayHandler.this.owner
												.removeRow(layout);
									}
								}
							});
				}
			});
		} else {
			msgDeleteBtn.setVisible(false);
		}

		rowLayout.addComponent(messageHeader);

		Label messageContent = new UrlDetectableLabel(comment.getComment());
		messageContent.setStyleName("message-body");
		rowLayout.addComponent(messageContent);

		List<Content> attachments = comment.getAttachments();
		if (!CollectionUtils.isEmpty(attachments)) {
			MVerticalLayout messageFooter = new MVerticalLayout().withSpacing(false).withMargin(true).withWidth
					("100%").withStyleName("message-footer");
			AttachmentDisplayComponent attachmentDisplay = new AttachmentDisplayComponent(
					attachments);
			attachmentDisplay.setWidth("100%");
			messageFooter.addComponent(attachmentDisplay);
			messageFooter.setComponentAlignment(attachmentDisplay,
					Alignment.MIDDLE_RIGHT);
			rowLayout.addComponent(messageFooter);
		}

		layout.addComponent(rowLayout);
		layout.setExpandRatio(rowLayout, 1.0f);
		return layout;
	}

	private boolean hasDeletePermission(SimpleComment comment) {
		return (AppContext.getUsername().equals(comment.getCreateduser()) || AppContext.isAdmin());
	}
}
