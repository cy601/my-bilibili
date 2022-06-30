package com.TanNgee.bilibili.service.util;

import com.TanNgee.bilibili.domain.exception.ConditionException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;

import java.util.Calendar;
import java.util.Date;


/**
 * @Author TanNgee
 * @Date 2022/6/22 23:01
 **/
public class TokenUtil {
    private static final String ISSUER = "签发者";

    /**
     * 使用 JWT 生成用户Token
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        return JWT.create().withKeyId(String.valueOf(userId)) // id
                .withIssuer(ISSUER)   // 签发者：机构或者个人
                .withExpiresAt(calendar.getTime())    // 过期时间
                .sign(algorithm); // 签名方法，这里用RSA
    }

    /**
     * 验证Token
     *
     * @param token
     * @return
     */
    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());  //Rsa解密
            JWTVerifier verifier = JWT.require(algorithm).build();  // 验证类

            DecodedJWT jwt = verifier.verify(token);  // 进行验证

            String userId = jwt.getKeyId();  //用户id

            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "token过期！");
        } catch (Exception e) {
            throw new ConditionException("非法用户token！");
        }
    }

    /**
     * 生成刷新token
     * @param userId
     * @return
     * @throws Exception
     */
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);   //有效期 7天
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
}
