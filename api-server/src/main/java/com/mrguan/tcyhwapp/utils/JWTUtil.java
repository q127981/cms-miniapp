package com.mrguan.tcyhwapp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

/**
 * jwt工具类
 *
 * @author MrGuan
 */
@Slf4j
public class JWTUtil {
    //秘钥
    public static final String SECRET_KEY = "tcyhw";
    //签发人
    private static final String ISSUER = "mrguan";

    /**
     * 生成签名
     */
    public static String generateToken(Long userId) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String token = JWT.create()
                //签发人
                .withIssuer(ISSUER)
                //签发时间
                .withIssuedAt(now)
                //过期时间
                .withExpiresAt(buildExpirationDate())
                //保存身份标识
                .withClaim("userId", userId)
                .sign(algorithm);
        return token;
    }

    public static Long getUserId() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(getToken());
            return jwt.getClaim("userId").asLong();
        } catch (Exception e) {
//            log.error(e.getMessage());
            throw new RuntimeException("获取用户信息异常！");
        }
    }

    private static Date buildExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 验证token
     */
    public static boolean verify(String token) {
        try {
            //算法
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception ex) {
            log.info("Token校验不通过:{}", token);
//            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 从token获取userId
     */
    public static Long getUserId(String token) {
        try {
            return JWT.decode(token).getClaim("userId").asLong();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0L;
    }

    public static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (ObjectUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        return token;
    }
}