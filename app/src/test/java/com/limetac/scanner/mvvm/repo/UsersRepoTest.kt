@file:Suppress("KDocMissingDocumentation")

package com.limetac.scanner.mvvm.repo

import androidx.annotation.NonNull
import io.reactivex.Observer;
import com.limetac.scanner.data.model.User
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.SingleObserver
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class UsersRepoTest {

    @Rule
    @JvmField
    val server = MockWebServer()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()


    @Before
    fun setUp() {
//        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
//        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        server.shutdown()
    }

    @Test
    fun testUserRepo() {
        val responseBody = """
                   [
                        {
                            "id": "1",
                            "name": "Dr. Edwin Orn",
                            "avatar": "https://s3.amazonaws.com/uifaces/faces/twitter/sunlandictwin/128.jpg",
                            "email": "Connor.Hartmann71@gmail.com"
                        }
                   ]
                    """.trimIndent()

        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(responseBody)
        )

        var response: List<User>? = null
        var error: Throwable? = null

        Rx2AndroidNetworking.get("https://5e510330f2c0d300147c034c.mockapi.io/users")
            .build()
            .getObjectListSingle(User::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                response = it
            }, {
                error = it
            })


        val request = server.takeRequest()
        assertThat(
            request.requestLine,
            equalTo("GET /1.1/favorites/create.json?id=1301054400182575104 HTTP/1.1")
        )
        Assert.assertNull(error)
        Assert.assertNotNull(response)
        Assert.assertEquals(response!![0].name, "Dr. Edwin Orn")

    }

    @Throws(InterruptedException::class)
    @Test
    fun testGetUsersRequest() {
        server.enqueue(MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"))
        val firstNameRef: AtomicReference<String> = AtomicReference()
        val lastNameRef: AtomicReference<String> = AtomicReference()
        val isSubscribedRef: AtomicReference<Boolean> = AtomicReference()
        val latch = CountDownLatch(1)

        Rx2AndroidNetworking.get(server.url("/").toString())
            .build()
            .jsonObjectSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<JSONObject> {
                override fun onSubscribe(@NonNull disposable: Disposable) {
                    isSubscribedRef.set(true)
                }

                override fun onSuccess(@NonNull response: JSONObject) {
                    try {
                        firstNameRef.set(response.getString("firstName"))
                        lastNameRef.set(response.getString("lastName"))
                        latch.countDown()
                    } catch (e: JSONException) {
                        assertTrue(false)
                    }
                }

                override fun onError(@NonNull throwable: Throwable) {
                    assertTrue(false)
                }
            })
        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertTrue(isSubscribedRef.get())
        assertEquals("Amit", firstNameRef.get())
        assertEquals("Shekhar", lastNameRef.get())
    }

//    @Throws(InterruptedException::class)
    @Test
    fun testJSONArrayGetRequest() {
        server.enqueue(MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"))
        val firstNameRef = AtomicReference<String>()
        val lastNameRef = AtomicReference<String>()
        val isSubscribedRef = AtomicReference<Boolean>()
        val isCompletedRef = AtomicReference<Boolean>()
        val latch = CountDownLatch(2)

        Rx2AndroidNetworking.get(server.url("/").toString())
            .build()
            .jsonArrayObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<JSONArray?> {
                override fun onSubscribe(d: Disposable) {
                    isSubscribedRef.set(true)
                }

                override fun onNext(t: JSONArray) {
                    try {
                        val jsonObject = t.getJSONObject(0)
                        firstNameRef.set(jsonObject.getString("firstName"))
                        lastNameRef.set(jsonObject.getString("lastName"))
                        latch.countDown()
                    } catch (e: JSONException) {
                        assertTrue(false)
                    }
                }

                override fun onError(e: Throwable) {
                    assertTrue(false)
                }

                override fun onComplete() {
                    isCompletedRef.set(true)
                    latch.countDown()
                }
            })

        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertTrue(isSubscribedRef.get())
        assertTrue(isCompletedRef.get())
        assertEquals("Amit", firstNameRef.get())
        assertEquals("Shekhar", lastNameRef.get())
    }

    @Test
    @Ignore
    fun testLogin_error() {
        /*  server.enqueue(
                  MockResponse()
                          .setResponseCode(400)
                          .setBody("{}")
          )

          var response: AuthenticationInfo? = null
          var error: Throwable? = null
          repo.login(LoginData("anton.holmberg@greenely.se", "password")).subscribeBy(
                  onNext = {
                      response = it
                  },
                  onError = {
                      error = it
                  }
          )

          assertThat(response).isNull()
          assertThat(error).isNotNull()
          assertThat(error).isInstanceOf(HttpException::class.java)*/
    }

    @Test
    @Ignore
    fun testLogin_noRetrofit() {
        /*    val responseBody = """
                                {
                                    "data": {
                                        "user_id": 1,
                                        "account_setup_next_v3": "HOUSEHOLD",
                                        "properties": {}
                                    },
                                    "jwt": "token"
                                }
                                """.trimIndent()

            server.enqueue(
                    MockResponse()
                            .setHeader("Content-Type", "application/json; charset=UTF-8")
                            .setBody(responseBody)
            )

            var response: AuthenticationInfo? = null
            var error: Throwable? = null
            repo.login(LoginData("anton.holmberg@greenely.se", "password")).subscribeBy(
                    onNext = {
                        response = it
                    },
                    onError = {
                        error = it
                    }
            )

            val request = server.takeRequest()

            assertThat(request.requestLine)
                    .isEqualTo("POST /v1/login HTTP/1.1")
            assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
            assertThat(request.body.readUtf8()).isEqualToIgnoringWhitespace(
                    """
                        {
                            "email": "anton.holmberg@greenely.se",
                            "password": "password"
                        }
                    """.trimIndent()
            )

            verify(userStore, times(1)).token = "token"

            assertThat(error).isNull()
            assertThat(response).isNotNull()

            assertThat(response).isEqualTo(
                    AuthenticationInfo(
                            1,
                            AccountSetupNext.HOUSEHOLD,
                            mapOf(),
                            null
                    )
            )*/
    }
}
