package trainsys.dao;

import trainsys.dao.entity.UserEntity;
import trainsys.dao.mapper.UserMapper;
import trainsys.dao.support.DbCodec;
import trainsys.model.UserInfo;
import trainsys.util.PasswordHasher;
import trainsys.util.Types.UserID;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManager {
    private final UserMapper userMapper;

    public UserManager(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(UserID userID, String username, String password, int privilege) {
        UserEntity entity = new UserEntity();
        entity.setUserId(userID.value());
        entity.setUsername(username);
        entity.setPassword(PasswordHasher.hash(password));
        entity.setPrivilege(privilege);
        userMapper.insert(entity);
    }

    public boolean existUser(UserID userID) {
        return userMapper.selectById(userID.value()) != null;
    }

    public UserInfo findUser(UserID userID) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity == null) {
            return null;
        }
        return DbCodec.toUserInfo(entity.getUserId(), entity.getUsername(), entity.getPassword(), entity.getPrivilege());
    }

    public UserInfo findUserByUsername(String username) {
        List<UserEntity> entities = userMapper.selectList(null);
        for (UserEntity entity : entities) {
            if (entity.getUsername() != null && entity.getUsername().equals(username)) {
                return DbCodec.toUserInfo(entity.getUserId(), entity.getUsername(), entity.getPassword(), entity.getPrivilege());
            }
        }
        return null;
    }

    public void removeUser(UserID userID) {
        userMapper.deleteById(userID.value());
    }

    public void modifyUserPrivilege(UserID userID, int newPrivilege) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity != null) {
            entity.setPrivilege(newPrivilege);
            userMapper.updateById(entity);
        }
    }

    public void modifyUserPassword(UserID userID, String newPassword) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity != null) {
            entity.setPassword(PasswordHasher.hash(newPassword));
            userMapper.updateById(entity);
        }
    }

    public void close() {
    }
}
