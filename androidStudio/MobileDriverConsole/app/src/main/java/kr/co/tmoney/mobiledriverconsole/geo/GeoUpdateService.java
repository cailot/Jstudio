package kr.co.tmoney.mobiledriverconsole.geo;

import android.app.IntentService;
import android.content.Intent;

import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 5..
 */
public class GeoUpdateService extends IntentService {

    private static final String LOG_TAG = MDCUtils.getLogTag(GeoUpdateService.class);

    public GeoUpdateService(){
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double latitude = intent.getDoubleExtra(MDCConstants.GEO_LOCATION_LATITUDE, 0);
        double longitude = intent.getDoubleExtra(MDCConstants.GEO_LOCATION_LONGITUDE, 0);

    }
}
