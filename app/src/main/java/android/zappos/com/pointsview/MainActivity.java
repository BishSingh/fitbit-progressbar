package android.zappos.com.pointsview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.zappos.com.pointsview.model.PointsResponse;
import android.zappos.com.pointsview.service.PointsService;
import android.zappos.com.pointsview.view.CircularProgressBar;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String S3_URL = "https://zappos-mobile.s3.amazonaws.com/android/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPoints()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<PointsResponse>() {
                            @Override
                            public void call(PointsResponse pointsResponse) {
                                if (pointsResponse == null || pointsResponse.getPointsToNextTier() == null
                                        || pointsResponse.getCurrentPoints() == null
                                        /**To avoid divide by zero in case of service issues.**/
                                        || pointsResponse.getCurrentPoints() + pointsResponse.getPointsToNextTier() == 0) {
                                    return;
                                }
                                CircularProgressBar circularProgressBar = findViewById(R.id.circular_progress_bar);
                                float progress = (float) pointsResponse.getCurrentPoints() /
                                        (float) (pointsResponse.getCurrentPoints() + pointsResponse.getPointsToNextTier());

                                circularProgressBar.setProgressWithAnimation(Math.round(progress * 100), pointsResponse.getCurrentPoints(),
                                        pointsResponse.getCurrentPoints() + pointsResponse.getPointsToNextTier());
                            }
                        }
                );
    }

    private Observable<PointsResponse> getPoints() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(S3_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        PointsService pointsService = retrofit.create(PointsService.class);

        return pointsService.getPoints();
    }
}
