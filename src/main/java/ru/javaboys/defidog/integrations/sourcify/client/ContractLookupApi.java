package ru.javaboys.defidog.integrations.sourcify.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientResponseException;
import ru.javaboys.defidog.integrations.sourcify.dto.GetContract200Response;
import ru.javaboys.defidog.integrations.sourcify.dto.GetV2ContractsChainId200Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-05-08T01:55:37.009934+03:00[Europe/Moscow]", comments = "Generator version: 7.7.0")
public class ContractLookupApi {
    private ApiClient apiClient;

    public ContractLookupApi() {
        this(new ApiClient());
    }

    @Autowired
    public ContractLookupApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Get verified contract
     * By default returns minimal information about the contract: &#x60;match&#x60;, &#x60;creation_match&#x60;, &#x60;runtime_match&#x60;, &#x60;chainId&#x60;, &#x60;address&#x60;, and &#x60;verifiedAt&#x60;  To get other details one can either list the fields requested in the &#x60;fields&#x60; query param or ask all fields but omit several with &#x60;omit&#x60;. To get everything just pass &#x60;fields&#x3D;all&#x60;.
     * <p><b>200</b> - Example response
     * <p><b>400</b> - Bad request from the client
     * <p><b>404</b> - The contract is not verified on Sourcify
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param address Contract&#39;s 20 byte address in hex string with the 0x prefix. Case insensitive.
     * @param fields Comma seperated fields to include in the response. Can also take &#x60;all&#x60;
     * @param omit Comma seperated fields to NOT include in the response. All fields except matching ones will be returned. Can&#39;t be used simultanously with &#x60;fields&#x60;.
     * @return GetContract200Response
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getContractRequestCreation(String chainId, String address, String fields, String omit) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'chainId' is set
        if (chainId == null) {
            throw new RestClientResponseException("Missing the required parameter 'chainId' when calling getContract", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'address' is set
        if (address == null) {
            throw new RestClientResponseException("Missing the required parameter 'address' when calling getContract", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("chainId", chainId);
        pathParams.put("address", address);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "fields", fields));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "omit", omit));
        
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<GetContract200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/v2/contract/{chainId}/{address}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get verified contract
     * By default returns minimal information about the contract: &#x60;match&#x60;, &#x60;creation_match&#x60;, &#x60;runtime_match&#x60;, &#x60;chainId&#x60;, &#x60;address&#x60;, and &#x60;verifiedAt&#x60;  To get other details one can either list the fields requested in the &#x60;fields&#x60; query param or ask all fields but omit several with &#x60;omit&#x60;. To get everything just pass &#x60;fields&#x3D;all&#x60;.
     * <p><b>200</b> - Example response
     * <p><b>400</b> - Bad request from the client
     * <p><b>404</b> - The contract is not verified on Sourcify
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param address Contract&#39;s 20 byte address in hex string with the 0x prefix. Case insensitive.
     * @param fields Comma seperated fields to include in the response. Can also take &#x60;all&#x60;
     * @param omit Comma seperated fields to NOT include in the response. All fields except matching ones will be returned. Can&#39;t be used simultanously with &#x60;fields&#x60;.
     * @return GetContract200Response
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public GetContract200Response getContract(String chainId, String address, String fields, String omit) throws RestClientResponseException {
        ParameterizedTypeReference<GetContract200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getContractRequestCreation(chainId, address, fields, omit).body(localVarReturnType);
    }

    /**
     * Get verified contract
     * By default returns minimal information about the contract: &#x60;match&#x60;, &#x60;creation_match&#x60;, &#x60;runtime_match&#x60;, &#x60;chainId&#x60;, &#x60;address&#x60;, and &#x60;verifiedAt&#x60;  To get other details one can either list the fields requested in the &#x60;fields&#x60; query param or ask all fields but omit several with &#x60;omit&#x60;. To get everything just pass &#x60;fields&#x3D;all&#x60;.
     * <p><b>200</b> - Example response
     * <p><b>400</b> - Bad request from the client
     * <p><b>404</b> - The contract is not verified on Sourcify
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param address Contract&#39;s 20 byte address in hex string with the 0x prefix. Case insensitive.
     * @param fields Comma seperated fields to include in the response. Can also take &#x60;all&#x60;
     * @param omit Comma seperated fields to NOT include in the response. All fields except matching ones will be returned. Can&#39;t be used simultanously with &#x60;fields&#x60;.
     * @return ResponseEntity&lt;GetContract200Response&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<GetContract200Response> getContractWithHttpInfo(String chainId, String address, String fields, String omit) throws RestClientResponseException {
        ParameterizedTypeReference<GetContract200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getContractRequestCreation(chainId, address, fields, omit).toEntity(localVarReturnType);
    }

    /**
     * Get verified contract
     * By default returns minimal information about the contract: &#x60;match&#x60;, &#x60;creation_match&#x60;, &#x60;runtime_match&#x60;, &#x60;chainId&#x60;, &#x60;address&#x60;, and &#x60;verifiedAt&#x60;  To get other details one can either list the fields requested in the &#x60;fields&#x60; query param or ask all fields but omit several with &#x60;omit&#x60;. To get everything just pass &#x60;fields&#x3D;all&#x60;.
     * <p><b>200</b> - Example response
     * <p><b>400</b> - Bad request from the client
     * <p><b>404</b> - The contract is not verified on Sourcify
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param address Contract&#39;s 20 byte address in hex string with the 0x prefix. Case insensitive.
     * @param fields Comma seperated fields to include in the response. Can also take &#x60;all&#x60;
     * @param omit Comma seperated fields to NOT include in the response. All fields except matching ones will be returned. Can&#39;t be used simultanously with &#x60;fields&#x60;.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getContractWithResponseSpec(String chainId, String address, String fields, String omit) throws RestClientResponseException {
        return getContractRequestCreation(chainId, address, fields, omit);
    }
    /**
     * List of verified contracts per chain
     * Retrieve the verified contracts on a chain
     * <p><b>200</b> - 
     * <p><b>400</b> - Bad request from the client
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param sort Sorts the contracts by most recent first (&#x60;desc&#x60;, default), or by oldest first (&#x60;asc&#x60;)
     * @param limit Number of contracts that should be returned per page. Maximum 200
     * @param afterMatchId The last &#x60;matchId&#x60; returned to get contracts older or newer than it (depending on &#x60;sort&#x60;)
     * @return GetV2ContractsChainId200Response
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getV2ContractsChainIdRequestCreation(String chainId, String sort, BigDecimal limit, String afterMatchId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'chainId' is set
        if (chainId == null) {
            throw new RestClientResponseException("Missing the required parameter 'chainId' when calling getV2ContractsChainId", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("chainId", chainId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "limit", limit));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "afterMatchId", afterMatchId));
        
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<GetV2ContractsChainId200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/v2/contracts/{chainId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List of verified contracts per chain
     * Retrieve the verified contracts on a chain
     * <p><b>200</b> - 
     * <p><b>400</b> - Bad request from the client
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param sort Sorts the contracts by most recent first (&#x60;desc&#x60;, default), or by oldest first (&#x60;asc&#x60;)
     * @param limit Number of contracts that should be returned per page. Maximum 200
     * @param afterMatchId The last &#x60;matchId&#x60; returned to get contracts older or newer than it (depending on &#x60;sort&#x60;)
     * @return GetV2ContractsChainId200Response
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public GetV2ContractsChainId200Response getV2ContractsChainId(String chainId, String sort, BigDecimal limit, String afterMatchId) throws RestClientResponseException {
        ParameterizedTypeReference<GetV2ContractsChainId200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getV2ContractsChainIdRequestCreation(chainId, sort, limit, afterMatchId).body(localVarReturnType);
    }

    /**
     * List of verified contracts per chain
     * Retrieve the verified contracts on a chain
     * <p><b>200</b> - 
     * <p><b>400</b> - Bad request from the client
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param sort Sorts the contracts by most recent first (&#x60;desc&#x60;, default), or by oldest first (&#x60;asc&#x60;)
     * @param limit Number of contracts that should be returned per page. Maximum 200
     * @param afterMatchId The last &#x60;matchId&#x60; returned to get contracts older or newer than it (depending on &#x60;sort&#x60;)
     * @return ResponseEntity&lt;GetV2ContractsChainId200Response&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<GetV2ContractsChainId200Response> getV2ContractsChainIdWithHttpInfo(String chainId, String sort, BigDecimal limit, String afterMatchId) throws RestClientResponseException {
        ParameterizedTypeReference<GetV2ContractsChainId200Response> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getV2ContractsChainIdRequestCreation(chainId, sort, limit, afterMatchId).toEntity(localVarReturnType);
    }

    /**
     * List of verified contracts per chain
     * Retrieve the verified contracts on a chain
     * <p><b>200</b> - 
     * <p><b>400</b> - Bad request from the client
     * <p><b>429</b> - You are sending too many requests to the server
     * <p><b>500</b> - 
     * @param chainId The chainId number of the EVM chain
     * @param sort Sorts the contracts by most recent first (&#x60;desc&#x60;, default), or by oldest first (&#x60;asc&#x60;)
     * @param limit Number of contracts that should be returned per page. Maximum 200
     * @param afterMatchId The last &#x60;matchId&#x60; returned to get contracts older or newer than it (depending on &#x60;sort&#x60;)
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getV2ContractsChainIdWithResponseSpec(String chainId, String sort, BigDecimal limit, String afterMatchId) throws RestClientResponseException {
        return getV2ContractsChainIdRequestCreation(chainId, sort, limit, afterMatchId);
    }
}
