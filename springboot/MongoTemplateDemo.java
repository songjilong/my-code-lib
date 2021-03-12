package ngp.bi.user.dto.menu;

import com.mongodb.BasicDBObject;
import ngp.bi.log.dto.PageDto;
import ngp.bi.user.dto.LockedUserDto;
import ngp.bi.user.entity.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author long
 * @date 2021/3/12
 */
public class MongoTemplateDemo {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取所有被锁定的用户，支持动态参数查询
     *
     * @param name 用户名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 起始页
     * @param size 每页数量
     * @return 分页结果
     */
    public PageDto<LockedUserDto> get(String name, Long startTime, Long endTime, int page, int size) {
        //只查询需要的字段值，减少内存占用
        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("id", true);
        fieldsObject.put("name", true);
        fieldsObject.put("alias", true);
        fieldsObject.put("roleId", true);
        fieldsObject.put("lockedTime", true);
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);

        //用户名筛选，模糊查询
        if(!StringUtils.isEmpty(name)) {
            query.addCriteria(Criteria.where("name").regex(getPattern(name)));
        }
        //大于等于开始时间
        if (startTime != null) {
            query.addCriteria(Criteria.where("lockedTime").gte(startTime));
        }
        //小于等于结束时间
        if (endTime != null) {
            query.addCriteria(Criteria.where("lockedTime").lte(startTime));
        }
        //按锁定时间倒序排列
        query.with(new Sort(Sort.Direction.DESC, "lockedTime"));
        //分页
        query.skip((page - 1) * size);
        query.limit(size);

        //查询总条数
        long total = mongoTemplate.count(query, UserEntity.class);

        //查询分页数据
        mongoTemplate.find(query, UserEntity.class);

        //封装分页对象
        PageDto<LockedUserDto> result = new PageDto<>();
        result.setTotal(total);
        result.setList(lockedUserDtoList);

        return result;
    }

    /**
     * 用于 mongoTemplate 模糊查询
     *
     * @param patternName 匹配字段
     * @return 匹配规则
     */
    private Pattern getPattern(String patternName) {
        return Pattern.compile("^.*" + patternName + ".*$", Pattern.CASE_INSENSITIVE);
    }
}
