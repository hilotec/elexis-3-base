package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.interfaces.IVerrechenbar.VatInfo;
import ch.elexis.core.ui.util.viewers.CommonViewer;

public class VATMenuContributionItem extends ContributionItem {
	
	CommonViewer cv;
	
	public VATMenuContributionItem(CommonViewer cv){
		this.cv = cv;
	}
	
	@Override
	public void fill(Menu menu, int index){
		StructuredSelection structuredSelection = new StructuredSelection(cv.getSelection());
		Object element = structuredSelection.getFirstElement();
		if (element instanceof ArtikelstammItem) {
			final ArtikelstammItem ai = (ArtikelstammItem) element;
			
			VatInfo vatInfo = ai.getVatInfo();
			
			MenuItem mi = new MenuItem(menu, SWT.None);
			mi.setText("MWSt. Satz für Artikel festlegen");
			mi.setEnabled(false);
			
			MenuItem vatNormal = new MenuItem(menu, SWT.RADIO);
			vatNormal.setText("Normal");
			vatNormal.setSelection(vatInfo.equals(VatInfo.VAT_CH_NOTMEDICAMENT));
			vatNormal.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					ai.overrideVatInfo(VatInfo.VAT_CH_NOTMEDICAMENT);
				}
			});
			
			MenuItem vatReduced = new MenuItem(menu, SWT.RADIO);
			vatReduced.setText("Reduziert");
			vatReduced.setSelection(vatInfo.equals(VatInfo.VAT_CH_ISMEDICAMENT));
			vatReduced.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					ai.overrideVatInfo(VatInfo.VAT_CH_ISMEDICAMENT);
				}
			});
			
			MenuItem vatNone = new MenuItem(menu, SWT.RADIO);
			vatNone.setText("Keine (0%)");
			vatNone.setSelection(vatInfo.equals(VatInfo.VAT_NONE));
			vatReduced.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					ai.overrideVatInfo(VatInfo.VAT_NONE);
				}
			});
		}
	}
	
	@Override
	public boolean isDynamic(){
		return true;
	}
}
