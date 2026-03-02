package aisato.ec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.User;

/**
 * ユーザーマーケティングインターフェース
 * データベースのusersテーブルへのアクセスを定義
 * @author zhl
 * @version 1.0.0
 * @Mapper インターフェースをMyBatisのマッパーとしてマークする。Springとの連携時に使⽤。
 */
@Mapper
public interface UserMapper {
    
    /**
     * ユーザー名またはメールアドレスでユーザーを検索
     * @param usernameOrEmail ユーザー名またはメールアドレス
     * @return ユーザーエンティティ
     */
    User findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * ユーザーIDでユーザーを検索
     * @param userId ユーザーID
     * @return ユーザーエンティティ
     */
    User findById(@Param("userId") Long userId);
    
    /**
     * ユーザーを登録
     * @param user ユーザーエンティティ
     * @return 影響を受けた行数
     */
    int insert(User user);
    
    /**
     * ユーザー情報を更新
     * @param user ユーザーエンティティ
     * @return 影響を受けた行数
     */
    int update(User user);
}