package net.zylk.alfresco.addons.scanner;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class CheckIfDocIsScannedEvaluator extends BaseEvaluator {
    private static final String ASPECT_SCANNED = "cm:scanned";

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try {

            JSONArray nodeAspects = getNodeAspects(jsonObject);
            if (nodeAspects == null) {
                return false;
            } else {
                if (nodeAspects.contains(ASPECT_SCANNED)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception err) {
            throw new AlfrescoRuntimeException("JSONException whilst running action evaluator: " + err.getMessage());
        }
    }
}