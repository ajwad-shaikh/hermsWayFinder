package ml.ajwad.hermswayfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] shortMessage;
            String sms_str ="";
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    assert pdus != null;
                    shortMessage = new SmsMessage[pdus.length];
                    for (int i=0; i<shortMessage.length; i++){
                        shortMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String messageBody = shortMessage[i].getMessageBody();
                        sms_str += messageBody;
                        Log.d("Receive Message", messageBody);
                        if(sms_str.startsWith("<hermsWay>")) {
                            sms_str += ("\n" + shortMessage[i].getMessageBody());
                            Intent smsIntent = new Intent("Inbox");
                            smsIntent.putExtra("message", sms_str);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                        }
                    }
                }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}