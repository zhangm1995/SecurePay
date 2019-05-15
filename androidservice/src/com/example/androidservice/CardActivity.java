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

//����ҳ��
public class CardActivity extends Activity {

	static CardActivity cardActivity;
	private ListView cardListView; // �����б�
	private String result = "";// ������
	private DBManager dbManager;
	List<Bank> cardItems;

	private int state;// ����״̬
	private String tel;
	private String situation;// ��ڣ�setting����ҳ���룬recharge��ֵ��withdraw����
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
		myBalance=intent.getDoubleExtra("balance", 0.0);//�˻����
		TextView title = (TextView) findViewById(R.id.cardTitle);
		if (!situation.equals("setting")) {
			title.setText("��ѡ�����п�");
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
			// �ӷ������˻�ȡ����
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
		// ʹ��Map��װ�������
		HashMap<String, String> map = new HashMap<String, String>();
		// ���巢�������URL
		String url = HttpUtil.Card_URL; // GET��ʽ
		try {
			// ��������
			result = HttpUtil.postRequest(url, map, dbManager); // GET��ʽ
			Log.d("����������ֵ", result);
			JSONObject jsonObject = new JSONObject(result);
			state = jsonObject.getInt("state");
			if (state == 1) {
				cards = jsonObject.getJSONArray("cards");
				if (cards.length() == 0 || cards == null) {
					// ��ʾδ�����п�����ת����ҳ��
					Intent bindingIntent = new Intent(CardActivity.this,
							BindingActivity.class);
					bindingIntent.putExtra("tel", tel);
					bindingIntent.putExtra("times", "one");// ��ӵĵ�һ�����п�
					startActivity(bindingIntent);
					finish();
				}
			}
		} catch (Exception e) {
		}

		cardListView = (ListView) findViewById(R.id.cardlist);
		// Ϊ�ؼ�����Textֵ
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
		// ���ӷ������˻�ȡ����jsonarry����Ϊlist
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

	// �̰��������
	public class myOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// positonΪ�������listView������
			Bank bank = cardItems.get(position);
			// ��ȡtitle��ֵ
			final String cardId = bank.getCardId();
			double cardBalance=bank.getBalance();
			if (situation.equals("setting")) {
				// �����������ҳ�棬��������󶨶Ի���
				new AlertDialog.Builder(CardActivity.this)
						.setTitle("�����")
						// ���öԻ������
						.setMessage("�Ƿ����ð����п���")
						// ������ʾ������
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {// ���ȷ����ť
									@Override
									public void onClick(DialogInterface dialog,
											int which) {// ȷ���������
										Log.i("alertdialog", cardId);
										// ʹ��Map��װ�������
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("tel", tel);
										map.put("cardId", cardId);
										// ���巢�������URL
										String url = HttpUtil.RelieveBinding_URL;
										String result;
										int state = 0;
										try {
											result = HttpUtil.postRequest(url,
													map, dbManager);
											Log.d("����������ֵ", result);
											JSONObject json = new JSONObject(
													result);
											state = json.getInt("state");// 0ʧ�ܣ�1�ɹ���2�绰�����п���Ϣ��һ��
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (state == 1) {
											// �󶨳ɹ�����ת
											NaviActivity.isBinding--;
											Toast.makeText(CardActivity.this,
													"����󶨳ɹ�",
													Toast.LENGTH_SHORT).show();
											try {
												getData();
											} catch (Exception e) {
												// block
												e.printStackTrace();
											}

										} else if (state == 2) {
											Toast.makeText(CardActivity.this,
													"���п���Ϣ����",
													Toast.LENGTH_SHORT).show();
										} else if (state == 3) {
											Toast.makeText(CardActivity.this,
													"δ�󶨸����п����޷������",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(CardActivity.this,
													"�����ʧ��",
													Toast.LENGTH_SHORT).show();
										}
									}

								})
						.setNegativeButton("����",
								new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť
									@Override
									public void onClick(DialogInterface dialog,
											int which) {// ��Ӧ�¼�
										Log.i("alertdialog", " ���أ�");
									}

								}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���
			} else {
				// ���ֻ��߳�ֵ����ת����Ӧҳ��
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
				Bank card = (Bank) mlistInfo.get(i); // ��ȡ��i������
				// ����makeItemView��ʵ����һ��Item
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

		// ����Item�ĺ���
		private View makeItemView(String cardId, String name, String tel,
				double amount) {
			LayoutInflater inflater = (LayoutInflater) CardActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// ʹ��View�Ķ���itemView��R.layout.item����
			View itemView = inflater.inflate(R.layout.vlist, null);

			// ͨ��findViewById()����ʵ��R.layout.item�ڸ����
			TextView cardIdView = (TextView) itemView.findViewById(R.id.arg0);
			cardIdView.setText("���п��ţ�" + cardId + ""); // ������Ӧ��ֵ
			TextView nameView = (TextView) itemView.findViewById(R.id.arg1);
			nameView.setText("�ֿ���������" + name);

			TextView telView = (TextView) itemView.findViewById(R.id.arg2);
			telView.setText("Ԥ���ֻ��ţ�" + tel + "");
			TextView balanceView = (TextView) itemView.findViewById(R.id.arg3);
			balanceView.setText("��" + amount + "Ԫ");

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
