package aisato.ec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * アイサットECサイト アプリケーション起動クラス
 * Spring Bootアプリケーションのエントリーポイント
 */
@SpringBootApplication
@MapperScan("aisato.ec.mapper")
@ComponentScan(basePackages = "aisato.ec")
public class AisattEcApplication extends SpringBootServletInitializer{

	  /**
     * アプリケーションのメインメソッド
     * Spring Bootアプリケーションを起動します
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(AisattEcApplication.class, args);
        System.out.println("========================================");
        System.out.println("  アイサット  オンラインショッピングシステム！");
        System.out.println("  http://localhost:8080/home/");
        System.out.println("========================================");
    }
}
