package com.coreteam.nfcshowcase

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var nfcAdapter: NfcAdapter
    var scanning = false
    val nfcUtils = NFCUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_scan_nfc.setOnClickListener {
            if (!scanning) {
                onScanNFC()
                btn_scan_nfc.text = "Cancelar"
            } else {

                btn_scan_nfc.text = "Escanear"
                txt_scanned.text = ""
            }
        }
    }

    private fun onScanNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter.equals(null)) {
            Toast.makeText(this, "Su dispositivo no cuenta con un " +
                    "lector NFC", Toast.LENGTH_SHORT).show()
            return
        }
        if (!nfcAdapter.isEnabled) {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            return
        }

        val pendingIntent = PendingIntent.getActivity(this, 500,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
        
        scanning = true
    }

    fun onCancelScan() {
        nfcAdapter.disableForegroundDispatch(this)
        scanning = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action != null) {
            when(intent.action) {
                NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                    val data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    if (data != null && data.isNotEmpty()) {
                        val message = data[0] as NdefMessage
                        var textObtained = ""
                        for (it in message.records) {
                            if (nfcUtils.isUri(it)) {
                                textObtained = nfcUtils.getUri(it)
                            } else if (nfcUtils.isText(it)) {
                                textObtained = nfcUtils.getText(it)
                            } else {
                                textObtained = String(it.payload)
                            }
                        }
                        txt_scanned.text = textObtained
                    }
                }
            }
        }
    }
}
