package github.com.asset;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;

public class Asset extends ChaincodeBase {

    /**
     * Init is called when initializing or updating chaincode. Use this to set
     * initial world state
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @return Response with message and payload
     */
    @Override
    public Response init(ChaincodeStub stub) {
        String fcn = stub.getFunction();
        List<String> params = stub.getParameters();
        //Adding one fish object to the ledger on init
        stub.putStringState("b0d9b2ec-562c-4917-9cd3-330248e73ace", "{\"docType\":\"fish\",\"id\":\"b0d9b2ec-562c-4917-9cd3-330248e73ace\",\"price\":4,\"type\":\"Salmon\",\"weight\":2}");
        return newSuccessResponse();
    }

    /**
     * Invoke is called to read from or write to the ledger
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @return Response
     */
    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            // Extract the function and args from the transaction proposal
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            switch (func) {
                case "set":
                    // Return result as success payload
                    return set(stub, params);
                case "get":
                    // Return result as success payload
                    return get(stub, params);
                case "delete":
                    // Return result as success payload
                    return delete(stub, params);
                case "query":
                    // Return result as success payload
                    return query(stub, params);
                default:
                    break;
            }
            //Error if unknown method
            return ChaincodeBase.newErrorResponse("Invalid invoke function name. Expecting one of: [\"set\", \"get\", \"delete\", \"query\"");
        } catch (Throwable e) {
            return ChaincodeBase.newErrorResponse(e.getMessage());
        }
    }

    /**
     * get receives the value of a key from the ledger
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @param args key
     * @return Response with message and payload
     */
    private Response get(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect arguments. Expecting a key");
        }

        String value = stub.getStringState(args.get(0));
        if (value == null || value.isEmpty()) {
            return newErrorResponse("Asset not found with key: " + args.get(0));
        }
        Response response = newSuccessResponse("Returned value for key : " + args.get(0) + " = " + value, value.getBytes(StandardCharsets.UTF_8));
        return response;
    }

    /**
     * Rich query using json to read from world state
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @param args json query
     * @return Response with message and payload
     */
    private Response query(ChaincodeStub stub, List<String> args) {
        String payload = "";

        //key value pair result iterator
        Iterator<KeyValue> iterator = stub.getQueryResult(args.get(0)).iterator();
        if (!iterator.hasNext()) {
            return newSuccessResponse("No results", "[]".getBytes(StandardCharsets.UTF_8));
        }
        while (iterator.hasNext()) {
            payload += iterator.next().getStringValue() + ",";
        }
        payload = payload.substring(0, payload.length() - 1);
        payload = "[" + payload + "]";

        Response response = newSuccessResponse("Query succesful", payload.getBytes(StandardCharsets.UTF_8));

        return response;
    }

    /**
     * set stores the asset (both key and value) on the ledger. If the key
     * exists, it will override the value with the new one
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @param args key and value
     * @return value
     */
    private Response set(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect arguments. Expecting a key and a value");
        }
        stub.putStringState(args.get(0), args.get(1));
        return newSuccessResponse("Succesfully set key : " + args.get(0) + " as value : " + args.get(1), args.get(1).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Delete the key from the state in ledger
     *
     * @param stub {@link ChaincodeStub} to operate proposal and ledger
     * @param args key
     * @return Response with message and payload
     */
    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting a key");
        }
        String key = args.get(0);
        // Delete the key from the state in ledger
        stub.delState(key);
        return newSuccessResponse("Succesfully deleted key : " + args.get(0) + "from the ledger", args.get(0).getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        new Asset().start(args);
    }
}
