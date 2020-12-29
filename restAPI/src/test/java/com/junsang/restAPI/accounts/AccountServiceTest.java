package com.junsang.restAPI.accounts;

import java.util.HashSet;
import java.util.Set;

import com.junsang.restAPI.common.TestDescription;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    /**
     * Service 테스트 이므로 MockMvc 사용이 필요 없음.
     */

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void findByUsername() {
        // Given
        String password = "p@ssw0rd";
        String username = "gufrus@naver.com";

        Set<AccountRole> roles = new HashSet<AccountRole>();
        roles.add(AccountRole.ADMIN);
        roles.add(AccountRole.USER);

        Account account = Account.builder()
                .email(username)
                .password(password)
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))   // JAVA 11
                .roles(roles)
                .build();

        this.accountRepository.save(account);

        // When
        //= accountService 객체 타입을 UserDetailsService 타입으로 변환
        UserDetailsService userDetailService = (UserDetailsService) accountService;
        //= username 호출
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        // Then
        //= 사용자가 입력한 패스워드와 UserDetailsService 인터페이스에서 읽어 들인 패스워드 비교 (패스워드 인코더 사용)
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

}