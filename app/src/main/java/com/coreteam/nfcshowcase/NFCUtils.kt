package com.coreteam.nfcshowcase

import android.graphics.Bitmap
import android.net.Uri
import android.nfc.NdefRecord
import android.util.SparseArray
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and


class NFCUtils {
    private val URI_PREFIX_MAP = SparseArray<String>()

    init {
            URI_PREFIX_MAP.append(0x00, "")
            URI_PREFIX_MAP.append(0x01, "http://www.")
            URI_PREFIX_MAP.append(0x02, "https://www.")
            URI_PREFIX_MAP.append(0x03, "http://")
            URI_PREFIX_MAP.append(0x04, "https://")
            URI_PREFIX_MAP.append(0x05, "tel:")
            URI_PREFIX_MAP.append(0x06, "mailto:")
            URI_PREFIX_MAP.append(0x07, "ftp://anonymous:anonymous@")
            URI_PREFIX_MAP.append(0x08, "ftp://ftp.")
            URI_PREFIX_MAP.append(0x09, "ftps://")
            URI_PREFIX_MAP.append(0x0A, "sftp://")
            URI_PREFIX_MAP.append(0x0B, "smb://")
            URI_PREFIX_MAP.append(0x0C, "nfs://")
            URI_PREFIX_MAP.append(0x0D, "ftp://")
            URI_PREFIX_MAP.append(0x0E, "dav://")
            URI_PREFIX_MAP.append(0x0F, "news:")
            URI_PREFIX_MAP.append(0x10, "telnet://")
            URI_PREFIX_MAP.append(0x11, "imap:")
            URI_PREFIX_MAP.append(0x12, "rtsp://")
            URI_PREFIX_MAP.append(0x13, "urn:")
            URI_PREFIX_MAP.append(0x14, "pop:")
            URI_PREFIX_MAP.append(0x15, "sip:")
            URI_PREFIX_MAP.append(0x16, "sips:")
            URI_PREFIX_MAP.append(0x17, "tftp:")
            URI_PREFIX_MAP.append(0x18, "btspp://")
            URI_PREFIX_MAP.append(0x19, "btl2cap://")
            URI_PREFIX_MAP.append(0x1A, "btgoep://")
            URI_PREFIX_MAP.append(0x1B, "tcpobex://")
            URI_PREFIX_MAP.append(0x1C, "irdaobex://")
            URI_PREFIX_MAP.append(0x1D, "file://")
            URI_PREFIX_MAP.append(0x1E, "urn:epc:id:")
            URI_PREFIX_MAP.append(0x1F, "urn:epc:tag:")
            URI_PREFIX_MAP.append(0x20, "urn:epc:pat:")
            URI_PREFIX_MAP.append(0x21, "urn:epc:raw:")
            URI_PREFIX_MAP.append(0x22, "urn:epc:")
            URI_PREFIX_MAP.append(0x23, "urn:nfc:")
    }


    fun isUri(record: NdefRecord) : Boolean {
        return record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                record.tnf == NdefRecord.TNF_ABSOLUTE_URI
    }

    fun getUri(record: NdefRecord) : String {
        if (isUri(record)) {
            var payload = record.payload
            if (record.tnf == NdefRecord.TNF_ABSOLUTE_URI) {
                return Uri.parse(Arrays.toString(payload)).toString()
            } else {
                if (Arrays.equals(record.type, NdefRecord.RTD_URI)) {
                    var prefix = URI_PREFIX_MAP.get(payload[0].toInt())
                    var fullUri = Arrays.toString(prefix.toByteArray(Charset.forName("UTF-8"))) +
                            Arrays.toString(Arrays.copyOfRange(payload, 1, payload.size))
                    return Uri.parse(fullUri).toString()
                }
            }
        }
        return Uri.EMPTY.toString()
    }

    fun isText(record: NdefRecord) : Boolean {
        return record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                Arrays.equals(record.type, NdefRecord.RTD_TEXT)
    }

    fun getText(record: NdefRecord) : String {
        val payload = record.payload
        val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
        val languageCodeLenght = payload[0].toInt() and 63
        val languageCode = String(payload, 1, languageCodeLenght, Charset.forName("US-ASCII"))
        val text = String(payload, languageCodeLenght + 1, payload.size - languageCodeLenght - 1,
            Charset.forName(textEncoding))
        return text;
    }
}