package trainsys.dao;

import trainsys.model.LoginRequest;
import trainsys.model.RegisterRequest;
import trainsys.model.UserInfo;
import trainsys.util.TrainSystem;
import trainsys.util.Types;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final SystemContextDao systemContextDao;

    public UserDao(SystemContextDao systemContextDao) {
        this.systemContextDao = systemContextDao;
    }

    public UserInfo login(LoginRequest request) {
        TrainSystem trainSystem = systemContextDao.getTrainSystem();
        // The HTTP layer is stateless, so each login attempt must start from a
        // clean in-memory user context instead of reusing the last request state.
        trainSystem.setCurrentUser(new UserInfo(new Types.UserID(-1L), "", "", 0));
        trainSystem.login(request.getAccount(), request.getPassword());
        UserInfo currentUser = trainSystem.getCurrentUser();
        trainSystem.setCurrentUser(new UserInfo(new Types.UserID(-1L), "", "", 0));
        return currentUser;
    }

    public long register(RegisterRequest request) {
        return systemContextDao.getTrainSystem().addUser(
                request.getUsername(),
                request.getPassword()
        );
    }

    public void logout() {
        systemContextDao.getTrainSystem().logout();
    }
}
