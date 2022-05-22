package com.example.AndroidMobileClient.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.AndroidMobileClient.R;
import com.example.AndroidMobileClient.Service.UserInformationService;
import com.example.AndroidMobileClient.Utils.SM2Utils;
import com.example.AndroidMobileClient.Utils.Util;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;

import java.io.IOException;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;

/**
 * 1、扫码获取数据
 * 2、将数据解析
 * 3、根据解析后的指令执行相应的密码操作
 * 4、使用HttpURLConnection发送数据到密码服务器
 * author: @DIWEIKA
 * time: 2021.5.18
 */

public class handleActivity extends AppCompatActivity {

    SM2Utils sm02 = new SM2Utils();

    private EditText editText;
    private Button yesBtn,noBtn;
    private ScrollView scrollView;
    String encryptData, decryptData, signature; //分别是加密后的数据、解密后的数据和数字签名
    String SharedPreferenceName; //定义sharedPreference的文件名
    boolean isValid; //这是验证数字签名是否有效的指标
    String[] signatureArray= new String[2];

    private String Privatekey="128B2FA8BD433C6C068C8D803DFF79792A519A55171B1B650C23661D15897263";
    private String Publickey="040AE4C7798AA0F119471BEE11825BE46202BB79E2A5844495E97C04FF4DF2548A7C0240F88F1CD4E16352A73C17B7F16F07353E53A176D684A9FE0C6BB798E857";

    //创建SharedPreferences对象
    private SharedPreferences mSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle);

         editText = (EditText) findViewById(R.id.edittext);
         yesBtn = (Button)findViewById(R.id.yesbtn);
         noBtn = (Button) findViewById(R.id.nobtn);
         scrollView = (ScrollView) findViewById(R.id.sv);

        mSharedPreference = getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);

        // Android 4.0 之后不能在主线程中请求HTTP请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    protected void onDestroy() {

        super.onDestroy();
    }

    //点击yes按钮，对数据进行解析，然后进行相关的密码操作
    @SuppressLint("WrongConstant")
    public void yes(View v) throws JSONException, IOException {

        //将私钥和公钥存入sharedPreference里
        savePrivatekey("ALICE123@YAHOO.COM",Privatekey);
        savePublickey("ALICE123@YAHOO.COM",Publickey);

        String scanData =getIntent().getStringExtra("scan_data");
        editText.setText(scanData);
        String[] array = scanData.split(" "); //将data解析成字符数组
        String[] usernameArray = array[0].split("="); //解析出用户名username
        SharedPreferenceName = usernameArray[1]; //将username赋值给SharedPreferenceName
        String[] IDArray = array[1].split("="); //解析出SessionID
        String[] dataArray = array[2].split("="); //从待加密数据字符串中解析出待密码处理数据
        String[] handleArray = array[3].split("="); //从处理字符串中解析出对数据进行何种处理（加密、解密、签名、验证）
        if(array.length==5){
            signatureArray = array[4].split("=");//从签名字符串中解析出签名
        }

        //判断对数据进行何种处理 handleArray[1]是解析出的字符串
        switch (handleArray[1]){
            case "SM2Encrypt" :
                encryptData = encrypt(dataArray[1],QueryPublicKey(usernameArray[1]));//如果操作是加密，那么对数据加密，返回密文
                editText.setText(encryptData);

                //下面使用HttpURLConnection的GET技术发送数据到Web密码服务器
                try {
                    boolean result = false;

                    String transData = "处理结果:"+encryptData+" "+"SessionId:"+IDArray[1];
                    result = UserInformationService.save("TransmittedData", transData);

                    if(result){
                        Toast.makeText(this, R.string.success, 1).show();
                    }else{
                        Toast.makeText(this, R.string.fail, 1).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.fail, 1).show();
                }

                break;

            case "SM2Decrypt":
                decryptData = decrypt(dataArray[1],QueryPrivateKey(usernameArray[1]));//如果操作是解密，那么对密文解密，返回明文
                editText.setText(decryptData);

                //下面使用HttpURLConnection的GET技术发送数据到Web密码服务器
                try {
                    boolean result = false;

                    String transData = "处理结果:"+decryptData+" "+"SessionId:"+IDArray[1];
                    result = UserInformationService.save("TransmittedData", transData);

                    if(result){
                        Toast.makeText(this, R.string.success, 1).show();
                    }else{
                        Toast.makeText(this, R.string.fail, 1).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.fail, 1).show();
                }

                break;

            case "SM2Sign":
                signature = sign(usernameArray[1],dataArray[1],QueryPrivateKey(usernameArray[1]));//如果操作是签名，那么根据username对数据签名，返回签名
                editText.setText(signature);

                //下面使用HttpURLConnection的GET技术发送数据到Web密码服务器
                try {
                    boolean result = false;

                    String transData = "处理结果:"+signature+" "+"SessionId:"+IDArray[1];
                    result = UserInformationService.save("TransmittedData", transData);

                    if(result){
                        Toast.makeText(this, R.string.success, 1).show();
                    }else{
                        Toast.makeText(this, R.string.fail, 1).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.fail, 1).show();
                }

                break;

            case "SM2Verify":
                isValid = verify(usernameArray[1],dataArray[1],QueryPublicKey(usernameArray[1]),signatureArray[1]);//如果操作是签名验证，那么根据username、原文、密钥对签名验证，返回验证结果
                if(isValid){
                    editText.setText("Valid");
                    //下面使用HttpURLConnection的GET技术发送数据到Web密码服务器
                    try {
                        boolean result = false;

                        String transData = "处理结果:"+"Valid"+" "+"SessionId:"+IDArray[1];
                        result = UserInformationService.save("TransmittedData", transData);

                        if(result){
                            Toast.makeText(this, R.string.success, 1).show();
                        }else{
                            Toast.makeText(this, R.string.fail, 1).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.fail, 1).show();
                    }
                }
                else {
                    editText.setText("Invalid");
                    //下面使用HttpURLConnection的GET技术发送数据到Web密码服务器
                    try {
                        boolean result = false;

                        String transData = "处理结果:"+"Invalid"+" "+"SessionId:"+IDArray[1];
                        result = UserInformationService.save("TransmittedData", transData);

                        if(result){
                            Toast.makeText(this, R.string.success, 1).show();
                        }else{
                            Toast.makeText(this, R.string.fail, 1).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.fail, 1).show();
                    }
                }

            default: break;
        }
    }

    //点击no按钮返回上一级
    public void no(View v){
        finish();
    }


    /**
     *
     * @param originaldata 原始数据
     * @param publickey 公钥
     * @return 16进制密文
     * @throws IOException
     */
    public String encrypt(String originaldata,String publickey) throws IOException {

        String publickeyS = new String(Base64.encode(Util.hexToByte(publickey)));
        byte[] encryptdata = sm02.encrypt(Base64.decode(publickeyS.getBytes()),originaldata.getBytes()); //SM2加密
        return Util.byteToHex(encryptdata);

    }

    /**
     *
     * @param encryptdata 密文
     * @param privatekey 私钥
     * @return 明文
     * @throws IOException
     */
    public String decrypt(String encryptdata,String privatekey) throws IOException {

        String privatekeyS = new String(Base64.encode(Util.hexToByte(privatekey)));
        byte[] encryptData = Util.hexStringToBytes(encryptdata);
        byte[] decryptdata = sm02.decrypt(Base64.decode(privatekeyS.getBytes()),encryptData); //SM2解密
        return Util.byteToString(decryptdata);
    }

    /**
     *
     * @param username
     * @param originaldata
     * @param privatekey
     * @return
     * @throws IOException
     */
    public String sign(String username,String originaldata,String privatekey) throws IOException {

        String privatekeyS = new String(Base64.encode(Util.hexToByte(privatekey)));
        byte[] signature = sm02.sign(username.getBytes(),Base64.decode(privatekeyS),originaldata.getBytes());
        return Util.getHexString(signature);
    }

    /**
     *
     * @param username
     * @param originaldata
     * @param publickey
     * @param signature
     * @return
     * @throws IOException
     */
    public boolean verify(String username,String originaldata,String publickey,String signature) throws IOException {

        String publickeyS = new String(Base64.encode(Util.hexToByte(publickey)));
        boolean isValid = sm02.verifySign(username.getBytes(),Base64.decode(publickeyS.getBytes()),originaldata.getBytes(),Util.hexStringToBytes(signature));
        return isValid;
    }

    /**
     * 存储Privatekey于sharedPreference里
     * 使用username-privateKey作为键名，Privatekey作为值
     * @param username
     * @param Privatekey
     */

    public  void savePrivatekey(String username, String Privatekey){
        SharedPreferences.Editor editor = mSharedPreference.edit();
        String key = username+"-privateKey";
        editor.putString(key, Privatekey);
        editor.commit();
    }

    /**
     * 存储Publickey于sharedPreference里
     * 使用username-publicKey作为键名，Publickey作为值
     * @param username
     * @param Publickey
     */
    public  void savePublickey(String username, String Publickey){
        SharedPreferences.Editor editor = mSharedPreference.edit();
        String key = username+"-publicKey";
        editor.putString(key, Publickey);
        editor.commit();
    }

    /**
     * 获取sharedPreference里的PublicKey
     * @param username
     * @return
     */
    public String QueryPublicKey(String username){
        String key = username+"-publicKey";
        return mSharedPreference.getString(key, null);
    }

    /**
     * 获取sharedPreference里的PrivateKey
     * @param username
     * @return
     */
    public String QueryPrivateKey(String username){
        String key = username+"-privateKey";
        return mSharedPreference.getString(key, null);
    }

}