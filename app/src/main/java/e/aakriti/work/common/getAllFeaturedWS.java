package e.aakriti.work.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;

import e.aakriti.work.adapter.FeaturedSliderPagerAdapter;
import e.aakriti.work.objects.FeaturedShows;
import e.aakriti.work.podcast_app.MainActivity;

public class getAllFeaturedWS extends AsyncTask<Void, Void, String> {

	private ProgressDialog mLoader;
	private String result = null, errorMessage = "",response = "";
	private int errorCode = 0;
	ViewPager pager;
	Context context;
	//ArrayList<Categories> allCategories;
	FeaturedSliderPagerAdapter adapter;
	Utility utility;

	public getAllFeaturedWS(ViewPager pager,Context c,FeaturedSliderPagerAdapter adapter) {
		// TODO Auto-generated constructor stub
		this.pager = pager;
		this.context = c;
		//this.allCategories = (ArrayList<Categories>) MainActivity.allCategories;
		this.adapter = adapter;
		utility = new Utility(context);
	}



	@Override
	protected void onPreExecute() {
		mLoader = new ProgressDialog(context);
		mLoader.setMessage("Loading");
		mLoader.setCancelable(false);
		mLoader.show();

		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (mLoader.isShowing())
			mLoader.dismiss();

		adapter = new FeaturedSliderPagerAdapter(context);
		pager.setAdapter(adapter);

		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			if (utility.isNetworkAvailable()) {
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String uri = RestApi.createURI(RestApi.GetAllFeaturedShows_WS);
							//String uri = "http://www.whooshkaa.com/index.php?r=api/LoginDevice&user_name="
							//	+ userName + "&password=" + passWord;
							result = RestApi.getDataFromURLWithoutParam(uri);

							if (Utility.isNotNull(result)) {
								final JSONObject objRes = new JSONObject(result);
								response = objRes.optString("respones");

								if (!response.equalsIgnoreCase("")) {
									((Activity) context).runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method
											// stub

											try {
												JSONArray jsonArray = new JSONArray(response);
												MainActivity.featured = new ArrayList<FeaturedShows>(jsonArray.length());
												for (int i =0 ;i<jsonArray.length();i++)
												{
													JSONObject obj = jsonArray.getJSONObject(i);
													FeaturedShows featured = new FeaturedShows(obj);
													MainActivity.featured.add(featured);
												}
												Log.e("featured", ""+MainActivity.featured.size());
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

										}
									});
								} else {
									((Activity) context).runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method
											// stub

											errorMessage = objRes.optString("msg");
											Toast.makeText(context, "" + errorMessage, Toast.LENGTH_LONG)
													.show();
										}
									});
								}
							}
						} catch (ConnectException e) {
							Log.e("", "" + e.toString());
						} catch (Exception e) {
							Log.e("", "" + e.toString());
						}
					}
				});
				th.start();
				th.join();
			} else {
				((Activity) context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "Please check your Internet connection",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
