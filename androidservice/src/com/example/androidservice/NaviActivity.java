package com.example.androidservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.model.Order;
import com.example.sqlite.DBManager;
import com.example.util.Encrypt;
import com.example.util.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//详情页面
public class NaviActivity extends Activity {

	static Activity MyNaviActivity;

	private ListView orderListView; // 订单列表
	private String result = "";// 请求结果
	private DBManager dbManager;

	private String state = "";// 请求状态
	private int userId = 0;// 用户账号
	private String userName = "";// 用户名
	private String tel = "";// 手机号
	private String email = "";// 邮箱
	private double balance = 0;// 用户余额
	private JSONArray orders;
	static int isBinding;

	TextView idTextView;
	TextView nameTextView;
	TextView balanceTextView;
	Button personButton;
	Button payButton;
	Button rechargeButton;
	Button withdrawButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_navi);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		dbManager = new DBManager(this);
		try {
			// 从服务器端获取数据
			getData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyNaviActivity = this;
		// listView.setOnCreateContextMenuListener(new
		// myOnCreateContextMenuListener());//设置长按监听器
		personButton = (Button) findViewById(R.id.personButton);
		personButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 如果校验成功
				Intent msgIntent = new Intent(NaviActivity.this,
						SettingActivity.class);
				msgIntent.putExtra("userName", userName);
				msgIntent.putExtra("tel", tel);
				msgIntent.putExtra("email", email);
				msgIntent.putExtra("isBinding", isBinding);
				msgIntent.putExtra("balance",balance);
				startActivity(msgIntent);
			}
		});
		// 转账
		payButton = (Button) findViewById(R.id.payButton);
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog(v);
			}

		});

		// 充值
		rechargeButton = (Button) findViewById(R.id.rechargeButton);
		rechargeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBinding == 0) {
					// 未绑定银行卡
					Toast.makeText(NaviActivity.this, "您未绑定银行卡！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 绑定了银行卡，跳转
					Intent cardIntent = new Intent(NaviActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "recharge");// 从充值页面入
					cardIntent.putExtra("balance", balance);
					startActivity(cardIntent);
				}
			}
		});

		// 提现
		withdrawButton = (Button) findViewById(R.id.withdrawButton);
		withdrawButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBinding == 0) {
					// 未绑定银行卡
					Toast.makeText(NaviActivity.this, "您未绑定银行卡！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 绑定了银行卡，跳转
					Intent cardIntent = new Intent(NaviActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "withdraw");// 从充值页面入
					cardIntent.putExtra("balance", balance);
					startActivity(cardIntent);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			try {
				getData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void getData() throws Exception {
		// 使用Map封装请求参数
		HashMap<String, String> map = new HashMap<String, String>();
		// 定义发送请求的URL
		String url = HttpUtil.Detail_URL; // GET方式
		try {
			// 发送请求
			result = HttpUtil.postRequest(url, map, dbManager); // GET方式
			Log.d("服务器返回值", result);
			AnalysisUser(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 如果token验证不成功，已过期，则删除数据库中token，启动Main Activity登录页面
			dbManager.delete();
			Intent nextIntent = new Intent(NaviActivity.this,
					MainActivity.class);
			NaviActivity.this.startActivity(nextIntent);
			// 结束该Activity
			finish();
		}
		if (state.equals("true")) {
			// 查询成功
		} else {
			// 查询不成功
			dbManager.delete();
			// 启动Main Activity
			Intent nextIntent = new Intent(NaviActivity.this,
					MainActivity.class);
			NaviActivity.this.startActivity(nextIntent);
			// 结束该Activity
			finish();
		}

		Log.d("navi的ID", String.valueOf(userId));
		Log.d("navi的name", userName);
		// 根据控件的ID得到响应的控件对象
		idTextView = (TextView) findViewById(R.id.idTextView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		balanceTextView = (TextView) findViewById(R.id.balanceTextView);
		orderListView = (ListView) findViewById(R.id.orderlist);
		// 为控件设置Text值
		idTextView.setText("账号：" + tel);
		nameTextView.setText("用户名：" + userName);
		balanceTextView.setText("余额：" + balance + "元");
		List<Order> orderItems = new ArrayList<Order>();
		try {
			orderItems = getList();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderListView.setAdapter(new OrderListViewAdapter(orderItems));
		orderListView.setOnItemClickListener(new myOnItemClickListener());

	}

	public List<Order> getList() throws JSONException {
		// 将从服务器端获取到的jsonarry解析为list
		List<Order> list = new ArrayList<Order>();
		for (int i = 0; i < orders.length(); i++) {
			Object o = orders.get(i);
			JSONObject jo1 = (JSONObject) o;
			Order order = new Order();
			order.setPayee(jo1.getString("payee"));
			order.setPayer(jo1.getString("payer"));
			order.setAmount(jo1.getDouble("amount"));
			order.setCreateTime(jo1.getString("createTime").substring(0, 19));
			list.add(order);
		}
		return list;
	}

	// 短按，即点击
	public class myOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// TODO Auto-generated method stub

			// super.onListItemClick(l, v, position, id);

		}
	}

	public class OrderListViewAdapter extends BaseAdapter {
		View[] itemViews;

		public OrderListViewAdapter(List<Order> mlistInfo) {
			// TODO Auto-generated constructor stub
			itemViews = new View[mlistInfo.size()];
			for (int i = 0; i < mlistInfo.size(); i++) {
				Order order = (Order) mlistInfo.get(i); // 获取第i个对象
				// 调用makeItemView，实例化一个Item
				itemViews[i] = makeItemView(i,order.getPayee(), order.getPayer(),
						order.getAmount(), order.getCreateTime());
			}
		}

		public int getCount() {
			return itemViews.length;
		}

		public View getItem(int position) {
			return itemViews[position];
		}

		public long getItemId(int position) {
			return position;
		}

		// 绘制Item的函数
		private View makeItemView(int i,String payee, String payer, double amount,
				String date) {
			LayoutInflater inflater = (LayoutInflater) NaviActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// 使用View的对象itemView与R.layout.item关联
			View itemView = inflater.inflate(R.layout.vlist, null);

			// 通过findViewById()方法实例R.layout.item内各组件
			TextView mpayer = (TextView) itemView.findViewById(R.id.arg0);
			mpayer.setText(i+1+"、付款人：" + payer + ""); // 填入相应的值
			TextView mpayee = (TextView) itemView.findViewById(R.id.arg1);
			mpayee.setText("收款人：" + payee + "");
			TextView mamount = (TextView) itemView.findViewById(R.id.arg2);
			String total = "";
			if (payer.equals(userName)) {
				// 付款,total减少
				total = "-";
				mamount.setTextColor(Color.parseColor("#A20055"));
			} else {
				total = "+";
				mamount.setTextColor(Color.parseColor("#550088"));
			}
			mamount.setText("交易金额：" + total + amount + "元");
			TextView dateTime = (TextView) itemView.findViewById(R.id.arg3);
			dateTime.setText(date);

			// ImageView image = (ImageView) itemView.findViewById(R.id.img);
			// image.setImageResource(resId);

			return itemView;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return itemViews[arg0];
		}

//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			if (convertView == null)
//				return itemViews[position];
//			return convertView;
//		}
	}

	/**
	 * 解析
	 * 
	 * @throws JSONException
	 */
	private void AnalysisUser(String jsonStr) throws JSONException {
		/******************* 解析 json串 ***********************/
		JSONObject jsonObject = new JSONObject(jsonStr);
		state = jsonObject.getString("state");
		if (state.equals("true")) {
			userName = jsonObject.getString("userName");
			tel = jsonObject.getString("tel");
			email= jsonObject.getString("email");
			userId = jsonObject.getInt("userId");
			balance = jsonObject.getDouble("balance");
			orders = jsonObject.getJSONArray("order");
			isBinding = jsonObject.getInt("isBinding");
		}

	}

	public void showAlertDialog(View view) {

		final CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage("请输入对方账号");
		builder.setInputType(2);
		builder.setTitle("请输入对方账号");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// 设置你的操作事项
				String tel = builder.getData();
				if (tel.equals("")) {
					Toast.makeText(NaviActivity.this, "请输入对方账号",
							Toast.LENGTH_SHORT).show();
				} else {
					// String url=HttpUtil.SelectUser_URL+"?tel="+tel;
					String url = HttpUtil.SelectUser_URL;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("tel", tel);
					try {
						String result = HttpUtil.postRequest(url, map,
								dbManager);
						JSONObject json = new JSONObject(result);
						int state = json.getInt("state");// 1是成功
						if (state == 1) {// 成功，跳转下一页面
							int payId = json.getInt("id");
							String payName = json.getString("name");
							String payTel = json.getString("tel");
							Intent payIntent = new Intent(NaviActivity.this,
									PayActivity.class);
							payIntent.putExtra("payId", payId);
							payIntent.putExtra("payName", payName);
							payIntent.putExtra("payTel", payTel);
							payIntent.putExtra("balance", balance);
							startActivity(payIntent);
						} else {
							// 失败，账号输入错误
							Toast.makeText(NaviActivity.this, "账号输入错误",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(NaviActivity.this, "账号输入错误",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}
}
