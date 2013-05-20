package com.fallen.streamgetter;




import com.fallen.streamgetterfree.R;
import com.google.android.vending.licensing.LicenseCheckerCallback;

public class MyLicenseCheckerCallback implements LicenseCheckerCallback{
	CallBackCheck cbc;
	public MyLicenseCheckerCallback(CallBackCheck cbc) {
		// TODO Auto-generated constructor stub
		this.cbc = cbc;
	}



	@Override
	public void allow(int reason) {
		// TODO Auto-generated method stub
		//System.out.println ("allow " + reason);
		showReason(reason);
	}



	@Override
	public void dontAllow(int reason) {
		// TODO Auto-generated method stub
		//System.out.println ("dont allow " + reason);
		
		showReason(reason);
	}

	@Override
	public void applicationError(int errorCode) {
		// TODO Auto-generated method stub
		//System.out.println ("Ошибка");
	}
	private void showReason(int reason) {
		// TODO Auto-generated method stub
		
		
		//System.out.println ("reason " + reason);
		cbc.onLicenseChecked(reason);


	}

}
