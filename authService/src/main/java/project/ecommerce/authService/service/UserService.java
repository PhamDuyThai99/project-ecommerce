package authService.src.main.java.project.ecommerce.authService.service;

@Service
@Slf4j
public class UserService {
    @Value("${externalApi.userService.getUserByUsernameUrl}")
    private static final String GET_USER_BY_USERNAME_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public UserInternalResponse getUserByUsernameFromUserService(String username) {
        String getUserUrl = GET_USER_BY_USERNAME_URL.replace("{username}", username);
        ApiResponse apiResponse;
        try {
            log.info("AuthenticationRequest {}", request);
            apiResponse = restTemplate.getForObject(getUserUrl, ApiResponse.class);
            log.info("found user apiResponse: " + apiResponse);
        } catch (RuntimeException ex) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }
        assert apiResponse != null;
        UserInternalResponse response = objectMapper.convertValue(apiResponse.getData(), UserInternalResponse.class);
        log.info("UserInternalResponse: " + response);
        return response;
    }
}
