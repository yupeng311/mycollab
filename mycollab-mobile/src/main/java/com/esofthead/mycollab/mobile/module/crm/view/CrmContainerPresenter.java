/**
 * This file is part of mycollab-mobile.
 *
 * mycollab-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.mobile.module.crm.view;

import com.esofthead.mycollab.common.ModuleNameConstants;
import com.esofthead.mycollab.mobile.MobileApplication;
import com.esofthead.mycollab.mobile.module.crm.ui.CrmGenericPresenter;
import com.esofthead.mycollab.mobile.shell.ModuleHelper;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.mvp.ScreenData;
import com.vaadin.ui.ComponentContainer;

/**
 * @author MyCollab Ltd.
 *
 * @since 4.4.0
 *
 */
public class CrmContainerPresenter extends
		CrmGenericPresenter<CrmContainerView> {

	private static final long serialVersionUID = -2422488836026839744L;

	public CrmContainerPresenter() {
		super(CrmContainerView.class);
	}

	@Override
	protected void onGo(ComponentContainer navigator, ScreenData<?> data) {
		ModuleHelper.setCurrentModule(view);
		super.onGo(navigator, data);
		// EventBusFactory.getInstance().post(
		// new AccountEvent.GotoList(navigator, data));
		String url = MobileApplication.getInstance().getInitialUrl();
		if (url != null && !url.equals("")) {
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			MobileApplication.rootUrlResolver.navigateByFragement(url);
		}

		AppContext.getInstance().updateLastModuleVisit(ModuleNameConstants.CRM);
	}

}