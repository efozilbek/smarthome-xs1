# How to use your own App and Intents to send commands to your XS1 #

Intents can be used in android to send commands through the smarthome app from your own app and receive status information


## How to use Intents from smarthome-xs1 ##

Intents from the app can be used to send command to and get info from the app.

### send a command ###
sending commands is done by sending an intent-object of type **com.android.xs.controller.SEND\_XS** to the app with to lists of extras.

One is of type
`ArrayList<String>`
and includes all names of the actuators to be changed or sensors to be read.

the other is of type
`ArrayList<Integer>`
and includes all values of the actuators to be set and 0 values for sensors. The two list have to be of same length!

### get the status ###
after sending a command the app will answer with an Intent with extra **"Status"** and a string with information if the action succeeded. Furthermore the answer will contain a StringArrayListExtra named **"Values"**. In there the names and values of the requested sensors will be stored in the pattern "**[name**];**[value**]".

### get a list of all actuators & sensors ###
if the sent list of names is empty the app will return a list of all actuators and sensors in the Values Extra in the format

|-from here actuators-|
|:--------------------|
|[name1](name1.md)    |
|[name2](name2.md)    |
|...                  |
|-from here sensors-  |
|[name4](name4.md)    |
|[name5](name5.md)    |
|...                  |


### execute a makro ###
for executing a makro just leave the the lists **names** and **vals** null and add a list named **makros** with the makro names to be executed.


### restrictions ###
both lists have to be of same length and the names of the actuators or sensors to be changed/read must be the same as defined in the xs-1, otherwise status intent will contain an error-message

### example code ###
```
public class XS1Intent extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_xs1_intent);

            final Intent intent = new Intent("com.android.xs.controller.SEND_XS");

            ArrayList<String> names = new ArrayList<String>();
            names.add("Staubsauger");
            names.add("Wohnzimmer");
            intent.putStringArrayListExtra("names", names);

            ArrayList<Integer> vals = new ArrayList<Integer>();
            vals.add(51);
            vals.add(0);
            intent.putIntegerArrayListExtra("vals", vals);

            //JUST ONE OF BOTH!!!

            ArrayList<String> makro = new ArrayList<String>();
            makro.add("num2");
            intent.putStringArrayListExtra("makros", makro);

            startActivityForResult(intent, 0);
    }

    @Override
    /**
     * Reads data scanned by user and returned by smarthome-xs1
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            TextView status = (TextView) findViewById(R.id.text);
            if (null != data && data.getExtras() != null) {
                    String result = data.getExtras().getString("Status");
                    
                    status.setText(result);
                    status.invalidate();
            }
            else 
                    status.setText("null");
            if (data.getStringArrayListExtra("Values").size() > 0)
            	Toast.makeText(this, data.getStringArrayListExtra("Values").get(0), Toast.LENGTH_LONG).show();
            else
            	Toast.makeText(this, "got no val", Toast.LENGTH_SHORT).show();
    }
}
```