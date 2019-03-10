package ru.eyelog.bottomsheetpaternkt

import android.content.Intent
import android.nfc.NfcAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomSheetDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import android.nfc.tech.MifareUltralight
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.app.PendingIntent
import android.nfc.Tag
import android.support.v4.app.FragmentActivity
import android.util.Log
import java.io.IOException
import java.nio.charset.Charset


class MainActivity : AppCompatActivity(), BSFragment.BottomSheetListener {

    lateinit var mPendingIntent: PendingIntent

    lateinit var mFilters: Array<IntentFilter>

    lateinit var nfcAdapter: NfcAdapter

    lateinit var mTechLists: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheet = BSFragment()

        buttonSDF.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "bottomSheet")
        }

        buttonSD.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            dialog.setContentView(dialogView)
            dialog.imageButton.setOnClickListener {
                textView.setText("Dialog tapped")
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onButtonClicked(text: String) {
        textView.setText(text)
    }

    fun nfcReader() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        var isNfcAvailable = nfcChecker(nfcAdapter)

        if (isNfcAvailable) {
            mPendingIntent = PendingIntent.getActivity(
                this, 0, Intent(
                    this,
                    javaClass
                ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )

            // Setup an intent filter for all MIME based dispatches
            val filter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            try {
                filter.addDataType("*/*")
            } catch (e: MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }

            mFilters = arrayOf(filter)

            // Setup a tech list for all NfcF tags
            mTechLists = arrayOf(
                arrayOf(
                    MifareUltralight::class.java
                        .name
                )
            )
        }
    }

    public override fun onResume() {
        super.onResume()
        /*if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(
                this, mPendingIntent, mFilters,
                mTechLists
            )*/
    }

    public override fun onNewIntent(intent: Intent?) {
        if (intent != null && intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {

            val extras = intent.extras
            val tag = extras!!.get(NfcAdapter.EXTRA_TAG) as Tag
            val ultralight = MifareUltralight.get(tag)

            textView.setText(
                "Discovered tag with intent: " + intent + "\n" +
                        " + tag: " + ultralight
            )
            val reader = readTag(tag)
            Toast.makeText(this, reader, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun readTag(tag: Tag): String {
        val mifare = MifareUltralight.get(tag)
        try {
            mifare!!.connect()
            val payload = mifare.readPages(4)
            return String(payload, Charset.forName("ISO-8859-1"))
        } catch (e: IOException) {
            Log.e(
                "Logcat:",
                "IOException while writing MifareUltralight  message...", e
            )
        } finally {
            if (mifare != null) {
                try {
                    mifare.close()
                } catch (e: IOException) {
                    Log.e("Logcat:", "Error closing tag...", e)
                }

            }
            return ""
        }
    }

    fun nfcChecker(nfcAdapter: NfcAdapter): Boolean {

        var isNfcAvailable = false

        // Is NFC available?
        if (nfcAdapter == null) {
            Toast.makeText(
                this, "А NFC-то и нет =(",
                Toast.LENGTH_SHORT
            ).show()
            isNfcAvailable = false
        } else {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(
                    this, "Включите  NFC.",
                    Toast.LENGTH_SHORT
                ).show();
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS));
            }
            // Доступны ли возможности Android Beam
            else if (!nfcAdapter.isNdefPushEnabled()) {
                // Если не включен
                Toast.makeText(
                    this, "Включите Android Beam.",
                    Toast.LENGTH_SHORT
                ).show();
                startActivity(Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            }
            isNfcAvailable = true
        }

        return isNfcAvailable
    }
}
