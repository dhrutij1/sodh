package com.sodh.com.sodh.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhruti on 29/12/16.
 */

@Aspect
@Component
@Configurable
public class AuditAspect {
	private static Logger logger = LoggerFactory.getLogger(AuditAspect.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private AgreementRepository agreementRepository;

	@Autowired
	private AgreementDocRepository agreementDocRepository;

	@Autowired
	private FailedLoginHistoryRepository failedLoginHistoryRepository;

	@Autowired
	private IpBlacklistRepository ipBlacklistRepository;

	@Autowired
	private ProfileAgreementRepository profileAgreementRepository;

	@Autowired
	private ProfileLockReasonsRepository profileLockReasonsRepository;

	@Autowired
	private ProfileQuestionRepository profileQuestionRepository;

	@Autowired
	private ReasonRepository reasonRepository;

	@Autowired
	private ReferralLinkRepository referralLinkRepository;

	@Autowired
	private SecurityQuestionRepository securityQuestionRepository;

	@Autowired
	private AuditTrailRepository auditTrailRepository;

	@Value("${audit.trail.enabled}")
	private String auditTrailEnabled;

	/*
	* Caution:
	* Note that the audit trail save call is excluded and if this is not done
	* the application can get stuck in an infinite loop.
	 */
	@Around(value = "execution(* com.cetera.cp.dao.*.save(*)) && " +
			"args(bean) && " +
			"!execution(* com.cetera.cp.dao.AuditTrailRepository.save(*))")
	public Object auditTrail(ProceedingJoinPoint joinPoint, Object bean) throws Throwable {

        /*
		* Activating / de-activating the audit trail feature depending upon the
        * application properties configuration.
         */
		Boolean performAuditing = Boolean.FALSE;

		if (auditTrailEnabled != null) {
			performAuditing = Boolean.valueOf(auditTrailEnabled);
		} else {
			performAuditing = Boolean.FALSE;
		}


        /*
        * Performing Audit Trail
         */
		if (performAuditing) {

			Object savedObject = null;
			Object objectId = null;
			Object returnedObject = null;
			String propertyValueJSON = null;
			AuditTrail auditTrail = null;
			Map<String, String> oldValues = new HashMap<>();
			Map<String, String> newValues = new HashMap<>();

			try {
                /*
                Pre-processing part
                 */

                /*
                * Identifying the object type that's being saved in the database and
                * fetching the database object with the same id, if present.
                 */
				objectId = BeanObjectComparator.getObjectId(bean);

                /*
                * Note:
                * The savedObject is re-assigned using a copy constructor of the relevant class
                * because whenever a database retrieve is done, the object memory used is persistent.
                * So, this hinders the later comparison between saved and new objects. We're effectively
                * just creating a copy of the saved object before the persistent memory is over-written.
                 */
				if (bean instanceof Profile) {
					savedObject = this.profileRepository.findById((String) objectId);
					savedObject = new Profile((Profile) savedObject);
				} else if (bean instanceof Agreement) {
					savedObject = this.agreementRepository.findById((Long) objectId);
					savedObject = new Agreement((Agreement) savedObject);
				} else if (bean instanceof AgreementDoc) {
					savedObject = this.agreementDocRepository.findById((Long) objectId);
					savedObject = new AgreementDoc((AgreementDoc) savedObject);
				} else if (bean instanceof FailedLoginHistory) {
					savedObject = this.failedLoginHistoryRepository.findById((Long) objectId);
					savedObject = new FailedLoginHistory((FailedLoginHistory) savedObject);
				} else if (bean instanceof IpBlacklist) {
					savedObject = this.ipBlacklistRepository.findByIp((String) objectId);
					savedObject = new IpBlacklist((IpBlacklist) savedObject);
				} else if (bean instanceof ProfileAgreement) {
					savedObject = this.profileAgreementRepository.findById((Long) objectId);
					savedObject = new ProfileAgreement((ProfileAgreement) savedObject);
				} else if (bean instanceof ProfileLockReason) {
					savedObject = this.profileLockReasonsRepository.findById((Long) objectId);
					savedObject = new ProfileLockReason((ProfileLockReason) savedObject);
				} else if (bean instanceof ProfileQuestion) {
					savedObject = this.profileQuestionRepository.findById((Long) objectId);
					savedObject = new ProfileQuestion((ProfileQuestion) savedObject);
				} else if (bean instanceof Reasons) {
					savedObject = this.reasonRepository.findById((Long) objectId);
					savedObject = new Reasons((Reasons) savedObject);
				} else if (bean instanceof ReferralLink) {
					savedObject = this.referralLinkRepository.findById((Long) objectId);
					savedObject = new ReferralLink((ReferralLink) savedObject);
				} else if (bean instanceof SecurityQuestion) {
					savedObject = this.securityQuestionRepository.findById((Long) objectId);
					savedObject = new SecurityQuestion((SecurityQuestion) savedObject);
				}

			} catch (Throwable e) {

				logger.debug("AUDIT TRAIL: Exception encountered in the pre-processing part");
				e.printStackTrace();

				// Audit trail should not interfere with application usage even if it fails.
				return joinPoint.proceed();
			}

            /*
            * Proceed with the join point function if there are no errors till this point.
             */
			returnedObject = joinPoint.proceed();

			try {
                /*
                * Post-processing part
                 */
				auditTrail = new AuditTrail();

				auditTrail.setTableName(bean.getClass().getName());

				if (objectId instanceof Long) {
					auditTrail.setObjectId(Long.toString((Long) objectId));
				} else {
					auditTrail.setObjectId((String) objectId);
				}


                /*
                * Identifying the client ip address.
                 */
				if (RequestContextHolder.getRequestAttributes() != null) {
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
							.getRequest();
					String ipAddress = request.getHeader("X-FORWARDED-FOR");
					if (ipAddress == null) {
						ipAddress = request.getRemoteAddr();
					}
					auditTrail.setIpAddress(ipAddress);
				}

                /*
                * Processing the created on and created by properties.
                 */
				Profile loggedInProfile = profileService.getLoggedInProfile();
				if (loggedInProfile != null) {
					auditTrail.setCreatedBy(loggedInProfile.getId());
				}
				auditTrail.setCreatedOn(new Date());


                /*
                * Comparing the previous database object values with the newly saved database
                * object properties.
                 */
				BeanObjectComparator.compareObjects(savedObject, returnedObject, oldValues, newValues);
				propertyValueJSON = new ObjectMapper().writeValueAsString(oldValues);
				auditTrail.setOldEntries(propertyValueJSON);
				propertyValueJSON = new ObjectMapper().writeValueAsString(newValues);
				auditTrail.setNewEntries(propertyValueJSON);

                /*
                * Saving the audit trail information in the audit trail table.
                 */
				auditTrailRepository.save(auditTrail);

				return returnedObject;

			} catch (Throwable e) {
				logger.debug("AUDIT TRAIL: Exception encountered in the post-processing part");
				e.printStackTrace();

				// Audit trail should not interfere with application usage even if it fails.
				return returnedObject;
			}

		} else {
            /*
            * Even if audit trail is disabled, yet the around point-cut would execute and it's
            * our responsibility to execute the joinpoint function and return the value.
             */
			return joinPoint.proceed();
		}

	}
}
