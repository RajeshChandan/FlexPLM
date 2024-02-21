package com.lowes.web.services;


import java.sql.Timestamp;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cdee.web.model.sample.SampleModel;
import com.lcs.wc.calendar.LCSCalendarTask;
import com.lcs.wc.calendar.LCSCalendarTaskLogic;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.sample.*;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.ptc.jws.ie.Collection;
import com.ptc.windchill.enterprise.search.server.impl.SearchResultsAugmenter;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import wt.lifecycle.LifeCycleHelper;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTIOException;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfEventConfiguration;
import wt.workflow.engine.WfOverdueActionType;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

@Path("/lowes")
@Api(value = "/lowes")
public class SampleRequestService {

    public static final String LCS_FLEXSPECIFICATION_ROOT_ID = "OR:com.lcs.wc.specification.FlexSpecification:";
    public static final String LCS_FLEXSPECIFICATION_IDA2A2 = "FlexSpecification.IDA2A2";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogR.getLogger(SampleRequestService.class.getName());
    private static final String FOLDERLOCATION = LCSProperties.get("com.lcs.wc.sample.LCSSample.rootFolder",
            "/Samples");

    @SuppressWarnings("unchecked")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/CreateMultipleSample")
    @ApiOperation(value = "POST operation on creating Sample in FlexPLM", notes = "Creates Sample", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header")})
    public Object createSample(
            @ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
            throws Exception {
        String sampleID = null;

        JSONObject jsonObject = null;
        JSONObject jsonRow = null;
        JSONObject scjson;
        LCSSeason season = null;
        LCSProduct product = null;
        final HashMap returnMap = new HashMap();
        FlexSpecification specification = null;
        LCSSourcingConfig srcConfig = null;
        LCSPartMaster productMaster = null;
        LCSSeasonMaster seasonMaster = null;
        String validJson = null;
        Map<Status, Object> response = new HashMap<Status, Object>();
        String error = null;
        LCSSampleRequest sampleRequest = null;

        String pid = null;
        String flexTypestr = null;
        String sid = null;
        String sourceId = null;
        JSONArray ProductArray = null;
        ArrayList<String> sampleIDList = new ArrayList<String>();

//
        jsonObject = new JSONObject(paramString);
        if (jsonObject.opt("Product") instanceof JSONArray) {
            ProductArray = jsonObject.optJSONArray("Product");
        }
        for (int i = 0; i < ProductArray.length(); i++) {

            try {
                // Get product
                jsonRow = ProductArray.optJSONObject(i);
                pid = jsonRow.optString("productId");
                sid = jsonRow.optString("seasonId");
                sourceId = jsonRow.optString("sourcingConfigId");
                flexTypestr = (String) jsonRow.optString("typeId");

                product = getProductFromID(pid);
                season = getSeasonFromID(sid);

                // Get source
                productMaster = (LCSPartMaster) product.getMaster();
                seasonMaster = product.getSeasonMaster();
                srcConfig = getSourcingFromID(sourceId);

                // Get spec
                specification = getSpecification(product, season, srcConfig);
            } catch (Exception e) {
                LOGGER.error(e);
            }
            if (product != null && season != null && srcConfig != null && specification != null) {

                sampleRequest = LCSSampleRequest.newLCSSampleRequest();
            }
            if (sampleRequest != null) {
                FlexType flexType = FlexTypeCache.getFlexType(flexTypestr);
                sampleRequest.setFlexType(flexType);
                LCSLogic.setFlexTypedDefaults(sampleRequest, SampleRequestFlexTypeScopeDefinition.SAMPLEREQUEST_SCOPE,
                        null);
                LCSLogic.assignFolder(FOLDERLOCATION, sampleRequest);
                sampleRequest.setOwnerMaster(productMaster);
                sampleRequest.setSourcingMaster(srcConfig.getMaster());
                sampleRequest.setSpecMaster(specification.getMaster());

                JSONObject sampleReqAtt = jsonRow.optJSONObject("SampleRequest");
                Iterator reqItr = sampleReqAtt.keys();
                while (reqItr.hasNext()) {
                    String key = (String) reqItr.next();
                    sampleRequest.setValue(key, sampleReqAtt.getString(key));
                }

                LCSSampleHelper.service.saveSampleRequest(sampleRequest, null);

                LCSSample sample = LCSSample.newLCSSample();

                sample.setFlexType(flexType);
                LCSLogic.setFlexTypedDefaults(sample, SampleRequestFlexTypeScopeDefinition.SAMPLE_SCOPE, null);
                LCSLogic.assignFolder(FOLDERLOCATION, sample);

                sample.setSampleRequest(sampleRequest);
                sample.setSourcingMaster(srcConfig.getMaster());
                sample.setSourcingMasterReference(srcConfig.getMasterReference());
                sample.setSpecMaster(specification.getMaster());
                sample.setSpecMasterReference(specification.getMasterReference());
                sample.setOwnerMaster(productMaster);
                sample.setOwnerMasterReference(product.getMasterReference());

                JSONObject sampleAtt = jsonRow.optJSONObject("Sample");
                Iterator sampleItr = sampleAtt.keys();
                while (sampleItr.hasNext()) {
                    String key = (String) sampleItr.next();
                    sample.setValue(key, sampleAtt.getString(key));
                }

                LCSSampleHelper.service.saveSample(sample);


                sampleID = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(sampleRequest));
                sampleIDList.add(sampleID);
                if (!sampleIDList.isEmpty()) {
                    returnMap.put("sampleFlexId", sampleIDList);
                }

            }

            if (error != null)
                returnMap.put("error", error);

            response = returnMap;
            LOGGER.debug("final response is : " + response);
        }
        return response;

    }

    public static LCSProduct getProductFromID(String styleId) throws WTException {
        LCSProduct product = null;
        if (FormatHelper.hasContent(styleId)) {
            product = (LCSProduct) LCSQuery.findObjectById(styleId);
            try {
                product = (LCSProduct) VersionHelper.getVersion(product, "A");
                product = (LCSProduct) VersionHelper.latestIterationOf(product);
            } catch (WTException e) {
            }

        }
        return product;
    }

    public static LCSSeason getSeasonFromID(String seasonId) throws WTException {
        LCSSeason season = null;
        if (FormatHelper.hasContent(seasonId)) {
            season = (LCSSeason) LCSQuery.findObjectById(seasonId);
            try {
                season = (LCSSeason) VersionHelper.latestIterationOf(season);
            } catch (WTException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
        return season;
    }

    public static LCSSourcingConfig getSourcingFromID(String sourceId) throws WTException {
        LCSSourcingConfig sourcingConfig = null;
        if (FormatHelper.hasContent(sourceId)) {
            sourcingConfig = (LCSSourcingConfig) LCSQuery.findObjectById(sourceId);
            try {
                sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourcingConfig);
            } catch (WTException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
        return sourcingConfig;
    }

    public static FlexSpecification getSpecification(LCSProduct product, LCSSeason season, LCSSourcingConfig srcConfig)
            throws WTException {
        FlexSpecification specification = null;
        final SearchResults sr = FlexSpecQuery.findExistingSpecs(product, season, srcConfig);
        List<Object> specs = new ArrayList<>(sr.getResults());

        for (Object obj : specs) {
            final FlexObject object = (FlexObject) obj;
            specification = (FlexSpecification) LCSQuery
                    .findObjectById(LCS_FLEXSPECIFICATION_ROOT_ID
                            + object.getData(LCS_FLEXSPECIFICATION_IDA2A2));
            final FlexSpecToSeasonLink spclink = FlexSpecQuery.findSpecToSeasonLink(specification.getMaster(),
                    season.getMaster());
            if (spclink.isPrimarySpec()) {
                break;
            }
        }
        return specification;
    }

    public static LCSSample createFitSamples(LCSSample sample, LCSProduct product, FlexSpecification specification,
                                             LCSSourcingConfig srcConfig) throws WTException {
        LCSSampleLogic sampleLogic = new LCSSampleLogic();

        LCSMeasurements measurements = LCSMeasurementsQuery.findMeasurements(product);
        if (measurements != null) {
            measurements = (LCSMeasurements) VersionHelper.latestIterationOf(measurements);
            String measurementsId = "OR:" + measurements;

            String size = measurements.getSampleSize();
            if (size != null) {
                sample = sampleLogic.createFitSample(sample, measurementsId, size);
            }

        }
        return sample;
    }
}

