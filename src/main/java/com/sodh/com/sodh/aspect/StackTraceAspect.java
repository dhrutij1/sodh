package com.sodh.com.sodh.aspect;

/**
 * Created by dhruti on 29/12/16.
 */
@Aspect
@Component
@Configurable
public class StackTraceAspect {
	private static Logger logger = LoggerFactory.getLogger(StackTraceAspect.class);

	@Autowired
	private NotificationService notificationService;

	@Value("${stack.trace.notif.recipient}")
	private String stackTraceNotifRecipient;

	@Value("${deploy.env}")
	private String deployEnv;

    /*
	Currently we are not intercepting all exceptions occurring in the application.
    If that is required, we can do away with separate interceptors for different exceptions
    and just have one generic interceptor instead.
     */

	/*
	Intercepting all PfmException instances thrown in the application scope.
	 */
	@AfterThrowing(pointcut = "execution(* com.cetera.cp..*(..))", throwing = "exception")
	public void forPfmException(PfmException exception) {
		notifyStackTrace(exception);
	}

	/*
	Intercepting all Null Pointer Exception instances thrown in the application scope.
	 */
	@AfterThrowing(pointcut = "execution(* com.cetera.cp..*(..))", throwing = "exception")
	public void forNullPointerException(NullPointerException exception) {
		notifyStackTrace(exception);
	}

	private void notifyStackTrace(Exception exception) {
		Map<String, String> mailVariables = new HashMap<>();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date());

		mailVariables.put("STACK_TRACE", sw.toString());
		mailVariables.put("LOG_TIME", timeStamp);
		mailVariables.put("ENVIRONMENT", deployEnv);

		notificationService.sendMail(stackTraceNotifRecipient, mailVariables, EmailTemplate.APPLICATION_ERROR);
	}
}

