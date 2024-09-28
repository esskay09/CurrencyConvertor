package com.example.currencyconvertor.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class PreferencesSerializer @Inject constructor() : Serializer<PreferencesProtoModel> {
    override val defaultValue: PreferencesProtoModel = PreferencesProtoModel.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PreferencesProtoModel =
        try {
            PreferencesProtoModel.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: PreferencesProtoModel, output: OutputStream) {
        t.writeTo(output)
    }
}
