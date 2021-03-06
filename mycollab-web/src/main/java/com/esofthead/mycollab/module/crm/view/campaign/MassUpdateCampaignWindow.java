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
package com.esofthead.mycollab.module.crm.view.campaign;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.CampaignWithBLOBs;
import com.esofthead.mycollab.module.crm.i18n.CampaignI18nEnum;
import com.esofthead.mycollab.module.crm.ui.CrmAssetsManager;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.ui.*;

/**
 * 
 * @author MyCollab Ltd.
 * @since 2.0
 * 
 */
public class MassUpdateCampaignWindow extends
		MassUpdateWindow<CampaignWithBLOBs> {
	private static final long serialVersionUID = 1L;

	public MassUpdateCampaignWindow(final String title,
			final CampaignListPresenter presenter) {
		super(title, CrmAssetsManager.getAsset(CrmTypeConstants.CAMPAIGN),
				new CampaignWithBLOBs(), presenter);
	}

	@Override
	protected IFormLayoutFactory buildFormLayoutFactory() {
		return new MassUpdateContactFormLayoutFactory();
	}

	@Override
	protected AbstractBeanFieldGroupEditFieldFactory<CampaignWithBLOBs> buildBeanFormFieldFactory() {
		return new CampaignEditFormFieldFactory<>(updateForm,
				false);
	}

	private class MassUpdateContactFormLayoutFactory implements
			IFormLayoutFactory {
		private static final long serialVersionUID = 1L;

		private GridFormLayoutHelper informationLayout;
		private GridFormLayoutHelper campaignGoal;

		@Override
		public ComponentContainer getLayout() {
			final VerticalLayout formLayout = new VerticalLayout();
			formLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

			final Label organizationHeader = new Label(
					AppContext
							.getMessage(CampaignI18nEnum.SECTION_CAMPAIGN_INFORMATION));
			organizationHeader.setStyleName(UIConstants.H2_STYLE2);
			formLayout.addComponent(organizationHeader);

			this.informationLayout = new GridFormLayoutHelper(2, 6, "100%",
					"167px", Alignment.TOP_LEFT);

			this.informationLayout.getLayout().setWidth("100%");
			this.informationLayout.getLayout().setMargin(false);
			this.informationLayout.getLayout().setSpacing(false);
			this.informationLayout.getLayout().addStyleName(
					"colored-gridlayout");
			formLayout.addComponent(this.informationLayout.getLayout());

			this.campaignGoal = new GridFormLayoutHelper(2, 6, "100%", "167px",
					Alignment.TOP_LEFT);
			final Label campaignMoreInfo = new Label(
					AppContext.getMessage(CampaignI18nEnum.SECTION_GOAL));
			campaignMoreInfo.setStyleName(UIConstants.H2_STYLE2);
			formLayout.addComponent(campaignMoreInfo);
			this.campaignGoal.getLayout().setWidth("100%");
			this.campaignGoal.getLayout().setMargin(false);
			this.campaignGoal.getLayout().setSpacing(false);
			this.campaignGoal.getLayout().addStyleName("colored-gridlayout");
			formLayout.addComponent(this.campaignGoal.getLayout());

			formLayout.addComponent(buildButtonControls());

			return formLayout;
		}

		@Override
		public void attachField(final Object propertyId, final Field<?> field) {

			if (propertyId.equals("assignuser")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(GenericI18Enum.FORM_ASSIGNEE), 0,
						0);
			}
			if (propertyId.equals("status")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(CampaignI18nEnum.FORM_STATUS), 1,
						0);
			} else if (propertyId.equals("type")) {
				this.informationLayout
						.addComponent(field, AppContext
								.getMessage(CampaignI18nEnum.FORM_TYPE), 0, 1);
			} else if (propertyId.equals("currencyid")) {
				this.campaignGoal.addComponent(field,
						AppContext.getMessage(CampaignI18nEnum.FORM_CURRENCY),
						0, 0);
			}

		}
	}
}
