package com.example.hahahaha.qrcodeadmin;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class  ReadData extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;

    JSONArray jsonArray;
    Dialog dialog;

    File file;

    Button btnUpDirectory,btnSDCard;

    ArrayList<String> pathHistory;
    String lastDirectory;
    int count = 0;

    ArrayList<XYValue> uploadData;


    ArrayList<ValueToServer> valueToServer;
    ListView lvInternalStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data);

        lvInternalStorage = (ListView) findViewById(R.id.lvInternalStorage);
        btnUpDirectory = (Button) findViewById(R.id.btnUpDirectory);
        btnSDCard = (Button) findViewById(R.id.btnViewSDCard);
        uploadData = new ArrayList<>();
        valueToServer=new ArrayList<>();
        jsonArray=new JSONArray();





        //need to check the permissions
        checkFilePermissions();

        lvInternalStorage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lastDirectory = pathHistory.get(count);
                if(lastDirectory.equals(adapterView.getItemAtPosition(i))){
                    Log.d(TAG, "lvInternalStorage: Selected a file for upload: " + lastDirectory);

                    //Execute method for reading the excel data.
                   // Toast.makeText(getApplicationContext(), "File Selected", Toast.LENGTH_SHORT).show();
                    Uri file = Uri.fromFile(new File(lastDirectory));
                    String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());
                    Log.e(TAG, "file extension is: "+ fileExt);
                    //new UploadFileAsync().execute(new JSONObject());
                    // Toast.makeText(ReadData.this,"file extension is:"+ fileExt, Toast.LENGTH_SHORT).show();
                    if(fileExt.equals("xlsx")){
                        //readExcelData(lastDirectory);
                        new UploadFileAsync().execute(new JSONObject());

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please select .xlsx file", Toast.LENGTH_SHORT).show();
                    }


                }else
                {
                    count++;
                    pathHistory.add(count,(String) adapterView.getItemAtPosition(i));
                    checkInternalStorage();
                    Log.d(TAG, "lvInternalStorage: " + pathHistory.get(count));
                }
            }
        });

        //Goes up one directory level
        btnUpDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 0){
                    Log.d(TAG, "btnUpDirectory: You have reached the highest level directory.");
                }else{
                    pathHistory.remove(count);
                    count--;
                    checkInternalStorage();
                    Log.d(TAG, "btnUpDirectory: " + pathHistory.get(count));
                }
            }
        });

        //Opens the SDCard or phone memory
        btnSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                pathHistory = new ArrayList<String>();
                pathHistory.add(count,System.getenv("EXTERNAL_STORAGE"));
                Log.d(TAG, "btnSDCard: " + pathHistory.get(count));
                checkInternalStorage();
            }
        });

    }

    /**
     *reads the excel file columns then rows. Stores data as ExcelUploadData object
     * @return
     */
    private void readExcelData(String filePath) {
        Log.d(TAG, "readExcelData: Reading Excel File.");
        Toast.makeText(getApplicationContext(), "Reading Excel File........", Toast.LENGTH_LONG).show();

        //decarle input file
        File inputFile = new File(filePath);

        try {
            InputStream inputStream = new FileInputStream(inputFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            //outter loop, loops through rows
            // String [] data=new String[rowsCount];

            for (int r = 1; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop, loops through columns
                for (int c = 0; c < 1; c++) {
                    //handles if there are to many columns on the excel sheet.
                    if(c>2){
                        Log.e(TAG, "readExcelData: ERROR. Excel File Format is incorrect! " );
                        toastMessage("ERROR: Excel File Format is incorrect!");
                        break;
                    }else{
                        String value = getCellAsString(row, c, formulaEvaluator);
                        String cellInfo = "r:" + r + "; c:" + c + "; v:" + value;
                        if(!(value==null ||value.equals(""))){
                            valueToServer.add(new ValueToServer(value));
                            Log.e(TAG, "readExcelDataValue:  Value is"+value );



                        }
                        else{
                            //valueToServer.add(new ValueToServer(value+"fake"));


                        }
                        Log.d(TAG, "readExcelData: Data from row: " + cellInfo);

                        sb.append(value + ", ");
                    }
                }
                sb.append(":");
            }
            Log.d(TAG, "readExcelData: STRINGBUILDER: " + sb.toString());

            //parseStringBuilder(sb);
            //printData();

            SendToServer();

        }catch (FileNotFoundException e) {
            Log.e(TAG, "readExcelData: FileNotFoundException. " + e.getMessage() );
        } catch (IOException e) {
            Log.e(TAG, "readExcelData: Error reading inputstream. " + e.getMessage() );
        }
    }

    private void SendToServer() {
        Toast.makeText(getApplicationContext(), "File has been read", Toast.LENGTH_SHORT).show();





            JSONArray js = new JSONArray();
            JSONObject obj = null;
        Log.e(TAG, "SendToServer: valueSize"+valueToServer.size() );
            for (int i = 0; i < valueToServer.size(); i++) {
                obj = new JSONObject();
                String send_data = valueToServer.get(i).getQrCode();

                try {
                    obj.put("QrCode", send_data);
                    js.put(i, obj);
                    //js.put(i,0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "SendToServer: " + js);
            //constant.serverurl
            sendDataToServer.sendsToServerFromExcel(getApplicationContext(), constant.serverurl, js, new serverDataCallback() {
                @Override
                public void OnSuccess(String message) {
                    Toast.makeText(getApplicationContext(), message+count, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnError(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                }
            });
        }





    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
     */
    public void parseStringBuilder(StringBuilder mStringBuilder){
        Log.d(TAG, "parseStringBuilder: Started parsing.");

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList<XYValue> row by row

        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");


            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                double x = Double.parseDouble(columns[0]);
                //double y = Double.parseDouble(columns[1]);
                double y=1;

                String cellInfo = "(x,y): (" + x + "," + y + ")";
                Log.d(TAG, "ParseStringBuilder: Data from row: " + cellInfo);

                //add the the uploadData ArrayList
                uploadData.add(new XYValue(x,y));

            }catch (NumberFormatException e){

                Log.e(TAG, "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }

        //printDataToLog();
    }
    private void printData(){
        for(int i=0;i<valueToServer.size();i++){
            String value=valueToServer.get(i).getQrCode();
            Log.d(TAG, "printData: Value:"+value);
        }
    }

    private void printDataToLog() {
        Log.d(TAG, "printDataToLog: Printing data to log...");

        for(int i = 0; i< uploadData.size(); i++){
            double x = uploadData.get(i).getX();
            double y = uploadData.get(i).getY();
            Log.d(TAG, "printDataToLog: (x,y): (" + x + "," + y + ")");
        }
    }

    /**
     * Returns the cell as a string from the excel file
     * @param row
     * @param c
     * @param formulaEvaluator
     * @return
     */
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {

            Log.e(TAG, "getCellAsString: NullPointerException: " + e.getMessage() );
        }
        return value;
    }

    private void checkInternalStorage() {
        Log.d(TAG, "checkInternalStorage: Started.");
        try{
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                toastMessage("No SD card found.");
            }
            else{
                // Locate the image folder in your SD Car;d
                file = new File(pathHistory.get(count));
                Log.d(TAG, "checkInternalStorage: directory path: " + pathHistory.get(count));
            }

            listFile = file.listFiles();

            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];

            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }

            for (int i = 0; i < listFile.length; i++)
            {
                Log.d("Files", "FileName:" + listFile[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilePathStrings);
            lvInternalStorage.setAdapter(adapter);

        }catch(NullPointerException e){
            Log.e(TAG, "checkInternalStorage: NULLPOINTEREXCEPTION " + e.getMessage() );
        }
    }

    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = 0;
            if  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");

                permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                if (permissionCheck != 0) {

                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
                }
            } else {
                Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
            }
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
    public void showLocationDialog(){
        dialog = new Dialog(ReadData.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.information_dialog);
        dialog.show();
    }


public void openHelp(View view) {
        showLocationDialog();

        }





             private class UploadFileAsync extends AsyncTask<JSONObject, Integer, JSONObject> {
                 private ProgressDialog progressDialog;
                 HttpClient httpClient = new DefaultHttpClient();
                 ProgressDialog pDialog;
                 ProgressBar bar;

                @Override
                protected JSONObject doInBackground(JSONObject... jsonObjects) {
                    Log.e(TAG, "doInBackground: Running" );
                    HttpResponse httpResponse = null;
                    HttpEntity httpEntity = null;
                    try {
                        String sourceFileUri = lastDirectory;

                        HttpURLConnection conn = null;
                        DataOutputStream dos = null;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int progress = 0;
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;

                        int maxBufferSize = 1 * 1024 * 1024;
                        File sourceFile = new File(sourceFileUri);

                        if (sourceFile.isFile()) {

                            try {
                                String upLoadServerUri = constant.serverurlfake;

                                // open a URL connection to the Servlet
                                FileInputStream fileInputStream = new FileInputStream(
                                        sourceFile);
                                URL url = new URL(upLoadServerUri);

                                // Open a HTTP connection to the URL
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true); // Allow Inputs
                                conn.setDoOutput(true); // Allow Outputs
                                conn.setUseCaches(false); // Don't use a Cached Copy
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Connection", "Keep-Alive");
                                conn.setRequestProperty("ENCTYPE",
                                        "multipart/form-data");
                                conn.setRequestProperty("Content-Type",
                                        "multipart/form-data;boundary=" + boundary);
                                conn.setRequestProperty("bill", sourceFileUri);

                                dos = new DataOutputStream(conn.getOutputStream());

                                dos.writeBytes(twoHyphens + boundary + lineEnd);
                                dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                        + sourceFileUri + "\"" + lineEnd);

                                dos.writeBytes(lineEnd);

                                // create a buffer of maximum size
                                bytesAvailable = fileInputStream.available();

                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                buffer = new byte[bufferSize];

                                // read file and write it into form...
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                                while (bytesRead > 0) {

                                    dos.write(buffer, 0, bufferSize);
                                    progress += bufferSize;
                                    publishProgress((int)(progress * 100 / bytesAvailable));
                                   // publishProgress(progress);
                                    Log.e(TAG, "doInBackground: Progress is :"+progress );
                                    bytesAvailable = fileInputStream.available();
                                    bufferSize = Math
                                            .min(bytesAvailable, maxBufferSize);
                                    bytesRead = fileInputStream.read(buffer, 0,
                                            bufferSize);

                                }

                                // send multipart form data necesssary after file
                                // data...
                                dos.writeBytes(lineEnd);
                                dos.writeBytes(twoHyphens + boundary + twoHyphens
                                        + lineEnd);

                                // Responses from the server (code and message)

                               //  serverResponseCode = conn.getResponseCode();
                                String serverResponseMessage = conn
                                        .getResponseMessage();
                                pDialog.dismiss();
                                Log.e(TAG, "doInBackground: "+conn.getResponseMessage() );

                               /* if (serverResponseCode == 200) {

                                    // messageText.setText(msg);
                                    //Toast.makeText(ctx, "File Upload Complete.",
                                    //      Toast.LENGTH_SHORT).show();

                                    // recursiveDelete(mDirectory1);

                                }*/

                                // close the streams //
                                fileInputStream.close();
                                dos.flush();
                                dos.close();

                            } catch (Exception e) {

                                // dialog.dismiss();
                                e.printStackTrace();


                            }
                            // dialog.dismiss();

                        } // End else block


                    } catch (Exception ex) {
                        // dialog.dismiss();

                        ex.printStackTrace();
                    }
                    return null ;
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                   // Log.e(TAG, "onPostExecute: "+result );

                    pDialog.dismiss();
                }




                 @Override
                protected void onPreExecute() {
                    bar=new ProgressBar(ReadData.this);

                     pDialog = new ProgressDialog(ReadData.this);
                     pDialog.setMessage("uploading Your File");
                     pDialog.setCancelable(false);
                     pDialog.setIndeterminate(false);
                     pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                     //Dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // pDialog.setMax(100);
                     //pDialog.setProgress(0);
                     pDialog.show();
                }

                @Override
                protected void onProgressUpdate(Integer... progress) {
                    pDialog.setProgress(progress[0]);
                    Log.e(TAG, "onProgressUpdate: "+progress[0] );
                    if(progress[0]==100){
                        pDialog.dismiss();
                    }
                }
            }
        }
