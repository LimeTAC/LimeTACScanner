package com.limetac.scanner.ui.view.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.limetac.scanner.R;
import com.limetac.scanner.reader.UHFBaseActivity;
import com.limetac.scanner.ui.view.antenna.AntennaActivity;
import com.limetac.scanner.ui.view.bin.BinActivity;
import com.limetac.scanner.ui.view.pkg.PackageScanningActivity;
import com.limetac.scanner.ui.view.scanHelper.ScanHelperActivity;
import com.limetac.scanner.ui.view.settings.SettingsActivity;
import com.limetac.scanner.ui.view.tag.TagScanActivity;
import com.limetac.scanner.ui.view.tagEntity.TagScanningActivity;
import com.limetac.scanner.utils.BluetoothUtil;
import com.limetac.scanner.utils.Preference;
import com.limetac.scanner.utils.ScreenUtils;
import com.limetac.scanner.utils.ToastUtil;
import com.rfidread.Interface.IAsynchronousMessage;
import com.rfidread.Models.Tag_Model;
import com.rfidread.RFIDReader;

import java.util.ArrayList;
import java.util.List;


public class MainMenuActivity extends UHFBaseActivity {

    CardView layoutPkgScan, layoutBin, layoutAntenna, layoutHelper, layout, layoutBluetooth, layoutTagEntity, layoutSettings;
    String scannerName = "";
    String newScannerName = "";
    Preference preference;
    String CURRENT_BLUETOOTH_NAME = "CURRENT_BLUETOOTH_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new Preference(this);
        String bluetoothName = preference.getStringPrefrence(CURRENT_BLUETOOTH_NAME, "");
        if (!bluetoothName.isEmpty()) {
            List<String> names = RFIDReader.GetBT4DeviceStrList();
            connectToDevice(bluetoothName);
        } else showDialog();

        initializeUI();
        setListeners();
    }

    private void showDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Bluetooth:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);


        arrayAdapter.addAll(getBluetoothDeviceNames());

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if (strName.equals(newScannerName))
                    strName = scannerName;
                connectToDevice(strName);


            }
        });
        builderSingle.show();
    }

    List<String> getBluetoothDeviceNames() {
        List<String> names = RFIDReader.GetBT4DeviceStrList();
        List<String> bluetoothName = new ArrayList<>();
        bluetoothName.addAll(names);

        for (String name : bluetoothName) {
            if (name.startsWith("BTR-")) {
                newScannerName = name.replace("BTR-", "LimeTAC-");
                scannerName = name;
            }
        }

        if (!scannerName.isEmpty() && !newScannerName.isEmpty()) {
            bluetoothName.remove(scannerName);
            bluetoothName.add(newScannerName);
        }
        return bluetoothName;
    }


    void connectToDevice(String bluetoothName) {
        if (Rfid_BT4_Init(bluetoothName, new IAsynchronousMessage() {
            @Override
            public void WriteDebugMsg(String s) {
//                    Toast.makeText(MainMenuActivity.this,"Debug:"+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void WriteLog(String s) {
                //  Toast.makeText(MainMenuActivity.this,"lOG:"+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void PortConnecting(String s) {
                //   Toast.makeText(MainMenuActivity.this,"cONNETED:"+s,Toast.LENGTH_LONG).show();

            }

            @Override
            public void PortClosing(String s) {

            }

            @Override
            public void OutPutTags(Tag_Model tag_model) {

            }

            @Override
            public void OutPutTagsOver() {

            }

            @Override
            public void GPIControlMsg(int i, int i1, int i2) {

            }

            @Override
            public void OutPutScanData(byte[] bytes) {
                //   Toast.makeText(MainMenuActivity.this,"sCANNER:",Toast.LENGTH_LONG).show();

            }
        })) {
            preference.saveStringInPrefrence(CURRENT_BLUETOOTH_NAME, bluetoothName);
            Toast.makeText(MainMenuActivity.this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainMenuActivity.this, "You are not connected with scanner device", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        UHF_Dispose();
        return true;
    }

    /**
     * release uhf rfid module
     */
    public void UHF_Dispose() {
        if (_UHFSTATE) {
            RFIDReader.CloseConn(ConnID);
            _UHFSTATE = false;
        }
    }


    private void setListeners() {

        layoutPkgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, PackageScanningActivity.class));
            }
        });

        layoutTagEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, TagScanningActivity.class));
            }
        });

        layoutAntenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, AntennaActivity.class));
            }
        });

        layoutBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, BinActivity.class));
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, TagScanActivity.class));
            }
        });

        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
            }
        });

        layoutHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, ScanHelperActivity.class));
            }
        });
        layoutBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothUtil.INSTANCE.isBluetoothEnabled())
                    showDialog();
                else
                    ToastUtil.createShortToast(MainMenuActivity.this, "Bluetooth not enabled");
            }
        });
    }

    private void initializeUI() {
        layoutPkgScan = findViewById(R.id.layoutPkgScan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        layoutBin = findViewById(R.id.layout);
        layoutHelper = findViewById(R.id.layoutPkgHelper);
        layoutAntenna = findViewById(R.id.layoutPkgAntenna);
        layout = findViewById(R.id.layoutTagScan);
        layoutBluetooth = findViewById(R.id.layoutBluetooth);
        layoutTagEntity = findViewById(R.id.layoutTagEntity);
        layoutSettings = findViewById(R.id.layoutSettings);
        setSupportActionBar(toolbar);
        setLayoutDimension(layoutAntenna);
        setLayoutDimension(layoutPkgScan);
        setLayoutDimension(layoutHelper);
        setLayoutDimension(layoutBin);
        setLayoutDimension(layoutBluetooth);
        setLayoutDimension(layout);
        setLayoutDimension(layoutTagEntity);
        setLayoutDimension(layoutSettings);


        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    void setLayoutDimension(CardView view) {
        view.getLayoutParams().width = (int) (ScreenUtils.getScreenWidth(this) * 0.39);
        view.getLayoutParams().height = (int) (ScreenUtils.getScreenWidth(this) * 0.3);
    }
}