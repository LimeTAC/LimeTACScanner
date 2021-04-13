package com.limetac.scanner.mvvm.api

import com.limetac.scanner.BuildConfig
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class APIAvailabilityTest {
    @Test
    @Throws(Exception::class)
    fun testPackageTagByCodeAvailability() {
        val connection: HttpURLConnection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/GetPackageTagsByPackageCode")).openConnection() as HttpURLConnection
        connection.doOutput = true;
        connection.requestMethod = "POST"
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json")
        val requestBody = """
                            {
	                            "packageCode": "nas270",
	                             "password": "!5353ns",
	                             "username": "eventreviewer1"
                            }
                            """.trimIndent()
        val outStream: OutputStream = connection.outputStream
        val outStreamWriter = OutputStreamWriter(outStream, "UTF-8")
        outStreamWriter.write(requestBody)
        outStreamWriter.flush()
        outStreamWriter.close()
        outStream.close()

/*        val charset = "UTF-8"
        var s = "unit_type=" + URLEncoder.encode(MainActivity.distance_units, charset)
        s += "&long=" + URLEncoder.encode(
            java.lang.String.valueOf(MainActivity.mLongitude),
            charset
        )
        s += "&lat=" + URLEncoder.encode(java.lang.String.valueOf(MainActivity.mLatitude), charset)
        s += "&user_id=" + URLEncoder.encode(java.lang.String.valueOf(MyndQuest.userId), charset)

        conn.setFixedLengthStreamingMode(s.toByteArray().size)
        val out = PrintWriter(conn.getOutputStream())
        out.print(s)
        out.close()*/
        val response = connection.inputStream
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testReleasePackageAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/ReleasePackageTag"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testPackageTagsByCodeAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/GetPackageTagsByTagCode"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testCreatePackageTagsAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/CreatePackageTags"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testPackageItemsAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/GetPackingItems"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testCreateDoubleReadBinTagsAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/CreateDoubleReadBinTags"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testReleaseEntityTagAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/ReleaseEntityTag"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testGetEntityTagsByCodeAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/GetEntityTagsByCode"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testCreateForkliftAntennaTagsAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/CreateForkliftAntennaTags"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllEntitiesByTagCodeAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/GetAllEntitiesByTagCode"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testCreateHelperLocationAvailability() {
        val connection =
            URL(BuildConfig.BASE_URL.plus("/api/inventory/CreateHelperLocation/"))
                .openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }


    @Test
    @Throws(Exception::class)
    fun testMockUsersAvailability() {
        val connection =
            URL("https://5e510330f2c0d300147c034c.mockapi.io/users").openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(
            InputStreamReader(
                response,
                Charset.defaultCharset()
            )
        ).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }
}