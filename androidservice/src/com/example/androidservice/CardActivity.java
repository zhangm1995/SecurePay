package com.example.androidservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.model.Bank;
import com.example.model.Order;
import com.example.sqlite.DBManager;
import com.example.util.Encrypt;
import com.example.util.HttpUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
public class CardActivity extends Activity {

	static CardActivity cardActivity;
	private ListView cardListView; // 订单列表
	private String result = "";// 请求结果
	private DBManager dbManager;
	List<Bank> cardItems;

	private int state;// 请求状态
	private String tel;
	private String situation;// 入口：setting设置页面入，recharge充值，withdraw提现
	private double myBalance;
	private JSONArray cards;
	Button addCardBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_card);
		dbManager = new DBManager(this);
		cardActivity = this;
		Intent intent = getIntent();
		tel = intent.getStringExtra("tel");
		situation = intent.getStringExtra("situation");
		myBalance=intent.getDoubleExtra("balance", 0.0);//账户余额
		TextView title = (TextView) findViewById(R.id.cardTitle);
		if (!situation.equals("setting")) {
			title.setText("请选择银行卡");
		}
		addCardBtn = (Button) findViewById(R.id.addCardButton);
		addCardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CardActivity.this,
						BindingActivity.class);
				intent.putExtra("tel", tel);
				intent.putExtra("times", "more");
				startActivity(intent);
			}
		});
		try {
			// 从服务器端获取数据
			getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			try {
				getData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void getData() throws Exception {
		// 使用Map封装请求参数
		HashMap<String, String> map = new HashMap<String, String>();
		// 定义发送请求的URL
		String url = HttpUtil.Card_URL; // GET方式
		try {
			// 发送请求
			result = HttpUtil.postRequest(url, map, dbManager); // GET方式
			Log.d("服务器返回值", result);
			JSONObject jsonObject = new JSONObject(result);
			state = jsonObject.getInt("state");
			if (state == 1) {
				cards = jsonObject.getJSONArray("cards");
				if (cards.length() == 0 || cards == null) {
					// 表示未绑定银行卡，跳转到绑定页面
					Intent bindingIntent = new Intent(CardActivity.this,
							BindingActivity.class);
					bindingIntent.putExtra("tel", tel);
					bindingIntent.putExtra("times", "one");// 添加的第一张银行卡
					startActivity(bindingIntent);
					finish();
				}
			}
		} catch (Exception e) {
		}

		cardListView = (ListView) findViewById(R.id.cardlist);
		// 为控件设置Text值
		cardItems = new ArrayList<Bank>();
		try {
			cardItems = getList();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		cardListView.setAdapter(new CardListViewAdapter(cardItems));
		cardListView.setOnItemClickListener(new myOnItemClickListener());

	}

	public List<Bank> getList() throws JSONException {
		// 将从服务器端获取到的jsonarry解析为list
		List<Bank> list = new ArrayList<Bank>();
		for (int i = 0; i < cards.length(); i++) {
			Object o = cards.get(i);
			JSONObject jo1 = (JSONObject) o;
			Bank card = new Bank();
			card.setCardId(jo1.getString("cardId"));
			card.setName(jo1.getString("name"));
			card.setTel(jo1.getString("tel"));
			card.setBalance(jo1.getDouble("balance"));
			list.add(card);
		}
		return list;
	}

	// 短按，即点击
	public class myOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// positon为点击到的listView的索引
			Bank bank = cardItems.get(position);
			// 获取title的值
			final String cardId = bank.getCardId();
			double cardBalance=bank.getBalance();
			if (situation.equals("setting")) {
				// 如果是在设置页面，弹出解除绑定对话框
				new AlertDialog.Builder(CardActivity.this)
						.setTitle("解除绑定")
						// 设置对话框标题
						.setMessage("是否解除该绑定银行卡？")
						// 设置显示的内容
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {// 添加确定按钮
									@Override
									public void onClick(DialogInterface dialog,
											int which) {// 确定，解除绑定
										Log.i("alertdialog", cardId);
										// 使用Map封装请求参数
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("tel", tel);
										map.put("cardId", cardId);
										// 定义发送请求的URL
										String url = HttpUtil.RelieveBinding_URL;
										String result;
										int state = 0;
										try {
											result = HttpUtil.postRequest(url,
													map, dbManager);
											Log.d("服务器返回值", result);
											JSONObject json = new JSONObject(
													result);
											state = json.getInt("state");// 0失败，1成功，2电话和银行卡信息不一致
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (state == 1) {
											// 绑定成功，跳转
											NaviActivity.isBinding--;
											Toast.makeText(CardActivity.this,
													"解除绑定成功",
													Toast.LENGTH_SHORT).show();
											try {
												getData();
											} catch (Exception e) {
												// block
												e.printStackTrace();
											}

										} else if (state == 2) {
											Toast.makeText(CardActivity.this,
													"银行卡信息错误",
													Toast.LENGTH_SHORT).show();
										} else if (state == 3) {
											Toast.makeText(CardActivity.this,
													"未绑定该银行卡，无法解除绑定",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(CardActivity.this,
													"解除绑定失败",
													Toast.LENGTH_SHORT).show();
										}
									}

								})
						.setNegativeButton("返回",
								new DialogInterface.OnClickListener() {// 添加返回按钮
									@Override
									public void onClick(DialogInterface dialog,
											int which) {// 响应事件
										Log.i("alertdialog", " 返回！");
									}

								}).show();// 在按键响应事件中显示此对话框
			} else {
				// 提现或者充值，跳转到相应页面
				Intent cardpayIntent=new Intent(CardActivity.this,CardPayActivity.class);
				cardpayIntent.putExtra("payTel", tel);
				cardpayIntent.putExtra("balance", myBalance);
				cardpayIntent.putExtra("cardId", cardId);
				cardpayIntent.putExtra("situation", situation);
				cardpayIntent.putExtra("cardBalance", cardBalance);
				startActivity(cardpayIntent);
			}

			// super.onListItemClick(l, v, position, id);

		}
	}

	public class CardListViewAdapter extends BaseAdapter {
		View[] itemViews;

		public CardListViewAdapter(List<Bank> mlistInfo) {
			itemViews = new View[mlistInfo.size()];
			for (int i = 0; i < mlistInfo.size(); i++) {
				Bank card = (Bank) mlistInfo.get(i); // 获取第i个对象
				// 调用makeItemView，实例化一个Item
				itemViews[i] = makeItemView(card.getCardId(), card.getName(),
						card.getTel(), card.getBalance());
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
		private View makeItemView(String cardId, String name, String tel,
				double amount) {
			LayoutInflater inflater = (LayoutInflater) CardActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// 使用View的对象itemView与R.layout.item关联
			View itemView = inflater.inflate(R.layout.vlist, null);

			// 通过findViewById()方法实例R.layout.item内各组件
			TextView cardIdView = (TextView) itemView.findViewById(R.id.arg0);
			cardIdView.setText("银行卡号：" + cardId + ""); // 填入相应的值
			TextView nameView = (TextView) itemView.findViewById(R.id.arg1);
			nameView.setText("持卡人姓名：" + name);

			TextView telView = (TextView) itemView.findViewById(R.id.arg2);
			telView.setText("预留手机号：" + tel + "");
			TextView balanceView = (TextView) itemView.findViewById(R.id.arg3);
			balanceView.setText("余额：" + amount + "元");

			// ImageView image = (ImageView) itemView.findViewById(R.id.img);
			// image.setImageResource(resId);

			return itemView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				return itemViews[position];
			return convertView;
		}
	}

}
