package com.dunn.tools.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

/**
 * 是基于activity测试的
 */
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PublicPermissionUtil{

}
/*
public class PublicPermissionActivity extends Activity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	//return:
	//false：可以执行
	//true：不能执行
	public boolean requestStoragePhone(String[] permisionPara) {
		boolean result = false;
		if(checkPermission(permisionPara)!=1) return result;

		result = true;
		//判断是否已经赋予权限
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
				||ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
			ActivityCompat.requestPermissions(this, permisionPara, 1);
		}
		return result;
	}

	//return:
	//-1：不满足条件，不检查权限
	//0：权限已经申请，不需要再申请
	//1：需要申请权限
	public int checkPermission(String[] permisionPara){
		int check=-1;

		if(permisionPara==null || permisionPara.length==0) return check;
		if(!PermissionTool.reqCheckPermission()) return check;
		if(!PermissionTool.reqCheckTarget(this)) return check;

		check = 0;
		for(String permission : permisionPara){
			if( PermissionManager.getInstance().isAppliedPermission(this, permission) == false){
				check = 1;
				break;
			}
		}

		return check;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			boolean result = false;
			for (int i = 0; i < permissions.length; i++) {
				if (grantResults[i] == PERMISSION_GRANTED) {
					LogTools.saveLog(LogTools.PERMISSION,"start ：success");
				} else {
					LogTools.saveLog(LogTools.PERMISSION,"start ：faild");
					result = true;
				}
			}
			if(result){
				askRationaleDialog(R.string.permission_dialog_context, R.string.permission_exstorage_phone);
			}else{
				reqSuccess();
			}
		}
	}

	public void reqSuccess(){

	}

	public void reqFail(){

	}

	private void askRationaleDialog(@StringRes int messageResId, @StringRes int context) {
		String title = String.format(getString(messageResId),getString(context));

		final NetWorkErrDialog mNetWorkErrDialog = new NetWorkErrDialog(this,getString(R.string.permission_dialog_set),title);
		mNetWorkErrDialog.setOkListenner(new RxOnFinishListenner<Boolean>(){
			@Override
			public void onFinish(Boolean aBoolean) {
				if(aBoolean){
					mNetWorkErrDialog.dismiss();
					settingPermissionActivity();
				}else{
					mNetWorkErrDialog.dismiss();
					reqFail();
				}
			}
		});
		mNetWorkErrDialog.show();
	}
	private void settingPermissionActivity() {
		//如果不是小米系统 则打开Android系统的应用设置页
		Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts ("package", getPackageName (), null);
		intent.setData (uri);
		startActivityForResult (intent, PermissionTool.CODE_REQUEST_CAMERA_PERMISSIONS);
	}
}
*/
