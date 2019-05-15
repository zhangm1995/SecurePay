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

//����ҳ��
public class NaviActivity extends Activity {

	static Activity MyNaviActivity;

	private ListView orderListView; // �����б�
	private String result = "";// ������
	private DBManager dbManager;

	private String state = "";// ����״̬
	private int userId = 0;// �û��˺�
	private String userName = "";// �û���
	private String tel = "";// �ֻ���
	private String email = "";// ����
	private double balance = 0;// �û����
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
			// �ӷ������˻�ȡ����
			getData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyNaviActivity = this;
		// listView.setOnCreateContextMenuListener(new
		// myOnCreateContextMenuListener());//���ó���������
		personButton = (Button) findViewById(R.id.personButton);
		personButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ���У��ɹ�
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
		// ת��
		payButton = (Button) findViewById(R.id.payButton);
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog(v);
			}

		});

		// ��ֵ
		rechargeButton = (Button) findViewById(R.id.rechargeButton);
		rechargeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBinding == 0) {
					// δ�����п�
					Toast.makeText(NaviActivity.this, "��δ�����п���",
							Toast.LENGTH_SHORT).show();
				} else {
					// �������п�����ת
					Intent cardIntent = new Intent(NaviActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "recharge");// �ӳ�ֵҳ����
					cardIntent.putExtra("balance", balance);
					startActivity(cardIntent);
				}
			}
		});

		// ����
		withdrawButton = (Button) findViewById(R.id.withdrawButton);
		withdrawButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBinding == 0) {
					// δ�����п�
					Toast.makeText(NaviActivity.this, "��δ�����п���",
							Toast.LENGTH_SHORT).show();
				} else {
					// �������п�����ת
					Intent cardIntent = new Intent(NaviActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "withdraw");// �ӳ�ֵҳ����
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
		// ʹ��Map��װ�������
		HashMap<String, String> map = new HashMap<String, String>();
		// ���巢�������URL
		String url = HttpUtil.Detail_URL; // GET��ʽ
		try {
			// ��������
			result = HttpUtil.postRequest(url, map, dbManager); // GET��ʽ
			Log.d("����������ֵ", result);
			AnalysisUser(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// ���token��֤���ɹ����ѹ��ڣ���ɾ�����ݿ���token������Main Activity��¼ҳ��
			dbManager.delete();
			Intent nextIntent = new Intent(NaviActivity.this,
					MainActivity.class);
			NaviActivity.this.startActivity(nextIntent);
			// ������Activity
			finish();
		}
		if (state.equals("true")) {
			// ��ѯ�ɹ�
		} else {
			// ��ѯ���ɹ�
			dbManager.delete();
			// ����Main Activity
			Intent nextIntent = new Intent(NaviActivity.this,
					MainActivity.class);
			NaviActivity.this.startActivity(nextIntent);
			// ������Activity
			finish();
		}

		Log.d("navi��ID", String.valueOf(userId));
		Log.d("navi��name", userName);
		// ���ݿؼ���ID�õ���Ӧ�Ŀؼ�����
		idTextView = (TextView) findViewById(R.id.idTextView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		balanceTextView = (TextView) findViewById(R.id.balanceTextView);
		orderListView = (ListView) findViewById(R.id.orderlist);
		// Ϊ�ؼ�����Textֵ
		idTextView.setText("�˺ţ�" + tel);
		nameTextView.setText("�û�����" + userName);
		balanceTextView.setText("��" + balance + "Ԫ");
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
		// ���ӷ������˻�ȡ����jsonarry����Ϊlist
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

	// �̰��������
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
				Order order = (Order) mlistInfo.get(i); // ��ȡ��i������
				// ����makeItemView��ʵ����һ��Item
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

		// ����Item�ĺ���
		private View makeItemView(int i,String payee, String payer, double amount,
				String date) {
			LayoutInflater inflater = (LayoutInflater) NaviActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// ʹ��View�Ķ���itemView��R.layout.item����
			View itemView = inflater.inflate(R.layout.vlist, null);

			// ͨ��findViewById()����ʵ��R.layout.item�ڸ����
			TextView mpayer = (TextView) itemView.findViewById(R.id.arg0);
			mpayer.setText(i+1+"�������ˣ�" + payer + ""); // ������Ӧ��ֵ
			TextView mpayee = (TextView) itemView.findViewById(R.id.arg1);
			mpayee.setText("�տ��ˣ�" + payee + "");
			TextView mamount = (TextView) itemView.findViewById(R.id.arg2);
			String total = "";
			if (payer.equals(userName)) {
				// ����,total����
				total = "-";
				mamount.setTextColor(Color.parseColor("#A20055"));
			} else {
				total = "+";
				mamount.setTextColor(Color.parseColor("#550088"));
			}
			mamount.setText("���׽�" + total + amount + "Ԫ");
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
	 * ����
	 * 
	 * @throws JSONException
	 */
	private void AnalysisUser(String jsonStr) throws JSONException {
		/******************* ���� json�� ***********************/
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
		builder.setMessage("������Է��˺�");
		builder.setInputType(2);
		builder.setTitle("������Է��˺�");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// ������Ĳ�������
				String tel = builder.getData();
				if (tel.equals("")) {
					Toast.makeText(NaviActivity.this, "������Է��˺�",
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
						int state = json.getInt("state");// 1�ǳɹ�
						if (state == 1) {// �ɹ�����ת��һҳ��
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
							// ʧ�ܣ��˺��������
							Toast.makeText(NaviActivity.this, "�˺��������",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(NaviActivity.this, "�˺��������",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}
}
