package com.homihq.db2rest.config;

import com.homihq.db2rest.auth.data.RoleDataFilter;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class MultiTenancyTest {

    private static final List<RoleDataFilter> FILTERS =
            List.of(new RoleDataFilter("tenant15", "db", "users", "tenant_id", "15"));

    private static Pair<List<RoleDataFilter>, String[]> roles(String... roles) {
        return Pair.of(FILTERS, roles);
    }

    @Test
    void groupsUserFilterBeforeAddingTenantFilter() {
        assertThat(MultiTenancy.joinFilters("auid==1,auid==2", "db", "users", roles("tenant15")))
                .isEqualTo("(auid==1,auid==2);tenant_id==15");
    }

    @Test
    void usesTenantFilterAloneWhenRequestHasNoFilter() {
        assertThat(MultiTenancy.joinFilters("", "db", "users", roles("tenant15")))
                .isEqualTo("tenant_id==15");
    }

    @Test
    void leavesFilterUntouchedWhenNoTenantFilterApplies() {
        assertThat(MultiTenancy.joinFilters("a==1,b==2", "db", "film", roles("tenant15")))
                .isEqualTo("a==1,b==2");
        assertThat(MultiTenancy.joinFilters("a==1,b==2", "db", "users", null))
                .isEqualTo("a==1,b==2");
    }

    @Test
    void usersWithoutTenantRoleOnlySeeNullTenantRows() {
        assertThat(MultiTenancy.joinFilters("a==1,b==2", "db", "users", roles("other")))
                .isEqualTo("(a==1,b==2);tenant_id=isnull=null");
    }

    @Test
    void rejectsFiltersThatCouldBreakOutOfTheGroup() {
        for (String filter : List.of("a==1),(b==2", "a==1)", "(a==1", "a=='x", "a==\"x")) {
            assertThatThrownBy(() -> MultiTenancy.joinFilters(filter, "db", "users", roles("tenant15")))
                    .as(filter)
                    .isInstanceOf(GenericDataAccessException.class);
        }
    }

    @Test
    void ignoresParenthesesAndEscapedQuotesInsideQuotedValues() {
        assertThatNoException().isThrownBy(() -> MultiTenancy.requireBalanced("name=='a)b',name==\"(c\""));
        assertThatNoException().isThrownBy(() -> MultiTenancy.requireBalanced("name=='it\\'s)'"));
        assertThatNoException().isThrownBy(() -> MultiTenancy.requireBalanced("(a==1,b==2);c==3"));
    }
}
