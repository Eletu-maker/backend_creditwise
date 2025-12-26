package com.creditwise.config;

import com.creditwise.entity.AuditLog;
import com.creditwise.repository.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingAspect.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Around("execution(* com.creditwise.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String userId = getCurrentUserId();
        
        // Log entry
        logger.info("Entering method: {}.{} with arguments: {}", 
                   className, methodName, Arrays.toString(joinPoint.getArgs()));
        
        // Create audit log entry
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action("METHOD_CALL")
                .resourceType(className)
                .details("Calling method: " + methodName + " with args: " + Arrays.toString(joinPoint.getArgs()))
                .build();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // Log exit
            logger.info("Exiting method: {}.{} with result: {} (Execution time: {} ms)", 
                       className, methodName, result, (endTime - startTime));
            
            // Update audit log with result
            auditLog.setDetails(auditLog.getDetails() + " | Result: " + result + " | Execution time: " + (endTime - startTime) + " ms");
            try {
                auditLogRepository.save(auditLog);
            } catch (Exception auditException) {
                // Log the audit error but don't fail the main operation
                logger.warn("Failed to save audit log: {}", auditException.getMessage());
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Exception in method: {}.{} with message: {}", 
                        className, methodName, e.getMessage());
            
            // Update audit log with error
            auditLog.setDetails(auditLog.getDetails() + " | Error: " + e.getMessage());
            try {
                auditLogRepository.save(auditLog);
            } catch (Exception auditException) {
                // Log the audit error but don't fail the main operation
                logger.warn("Failed to save audit log: {}", auditException.getMessage());
            }
            
            throw e;
        }
    }
    
    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.creditwise.security.CustomUserDetails) {
                com.creditwise.security.CustomUserDetails customUserDetails = 
                    (com.creditwise.security.CustomUserDetails) authentication.getPrincipal();
                return customUserDetails.getUserId().toString();
            } else if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                return user.getUsername(); // In a real app, this would be the user ID
            }
        } catch (Exception e) {
            logger.warn("Could not get current user ID: {}", e.getMessage());
        }
        return "anonymous";
    }
}