package com.zjs.mj.schedulings;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjs.mj.Bot.QQBot;
import com.zjs.mj.config.Properties;
import com.zjs.mj.constant.UserRole;
import com.zjs.mj.entity.RecentJob;
import com.zjs.mj.entity.Task;
import com.zjs.mj.entity.dto.DefaultRoleCount;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.exception.WrongRequestInfoException;
import com.zjs.mj.mapper.DefaultRoleCountMapper;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulingTask {

    private static String url = "https://www.midjourney.com/api/app/recent-jobs/?amount=10&dedupe=true&jobStatus=completed&orderBy=new&prompt=undefined&refreshApi=0&searchType=advanced&service=null&type=all&userId={userId}&user_id_ranked_score=null&_ql=todo&_qurl=https%3A%2F%2Fwww.midjourney.com%2Fapp%2F";

    private final Properties properties;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final DefaultRoleCountMapper countMapper;

    /**
     * 每个月一号零点更新普通用户的fast余额
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateFastCount() {
        log.info("update user fast count");
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getRole, UserRole.NORMAL);
        List<User> users = userMapper.selectList(userWrapper);
        if (users.size() > 0) {
            LambdaQueryWrapper<DefaultRoleCount> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.in(DefaultRoleCount::getRole, UserRole.NORMAL);
            DefaultRoleCount normalCount = countMapper.selectOne(countWrapper);

            for (User user : users) {
                user.setFastCount(normalCount.getFastCount());
                userMapper.updateById(user);
            }
        }
    }

    /**
     * 每天零点更新用户relax余额
     * 每天凌晨0点更新
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRelaxCount() {
        log.info("update user relax count");
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getRole, UserRole.NORMAL, UserRole.PLUS);
        List<User> users = userMapper.selectList(userWrapper);
        if (users.size() > 0) {
            LambdaQueryWrapper<DefaultRoleCount> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.in(DefaultRoleCount::getRole, UserRole.NORMAL, UserRole.PLUS);
            List<DefaultRoleCount> counts = countMapper.selectList(countWrapper);
            DefaultRoleCount normal = null;
            DefaultRoleCount plus = null;
            for (DefaultRoleCount count : counts) {
                if (count.getRole().equals(UserRole.NORMAL)) {
                    normal = count;
                } else {
                    plus = count;
                }
            }
            for (User user : users) {
                if (user.getRole().equals(UserRole.NORMAL)) {
                    user.setRelaxCount(normal.getRelaxCount());
                } else {
                    user.setRelaxCount(plus.getRelaxCount());
                }
                userMapper.updateById(user);
            }

        }

    }

    /**
     * 定时查recent job
     * 用于恢复没有被机器人监听到的任务 (例如机器人收不到mj私发给用户的作图结果)
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void queryRecentJobs() {
        if (!QQBot.ok) {
            log.info("机器人未登录，跳过查询recent job");
            return;
        }
        try {
            request();
        } catch (Exception e) {
            log.error("查询recent job失败", e);
        }
    }

    private void request() {
        Properties.MjConfig mj = properties.getMj();
        String userId = mj.getUserId();

        if (url == null || url.length() == 0) {
            log.error("url为空");
            throw new WrongRequestInfoException();
        }
        url = url.replace("{userId}", userId);

        HttpRequest request = HttpUtil.createPost(url).header("Cookie", "__Secure-next-auth.session-token=" + mj.getToken());
        try {
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("请求失败，状态码：{}", response.getStatus());
                return;
            }
            String body = response.body();
            List<RecentJob> jobs = parse(body);
            if (jobs == null || jobs.isEmpty()) {
                return;
            }
            for (RecentJob job : jobs) {
                LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<Task>();
                queryWrapper.eq(Task::getRootTaskId, job.getTaskId())
                        .eq(Task::getAction, job.getAction())
                        .eq(Task::isPosted,false)
                        .notIn(Task::getStatus, TaskStatus.SUCCESS, TaskStatus.FAILED);
                Task task = taskMapper.selectOne(queryWrapper);

                if (task != null) {
                    log.info("find the task deleted, taskId:{}", task.getTaskId());
                    task.setStatus(TaskStatus.SUCCESS);
                    task.setDescription("recover from recent job");
                    task.setMessageHash(job.getMessageHash());
                    task.setRequestId(job.getMessageId());
                    task.setImageUrl(job.getImageUrl());
                    task.setDeleted(true);
                    task.setFinishTime(LocalDateTime.now());
                    task.notifyUser();
                    taskMapper.updateById(task);
                }
            }
        } catch (Exception e) {
            log.error("查询最近Job失败", e);
        }
    }


    private List<RecentJob> parse(String body) {
        List<RecentJob> recentJobs = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Map<String, Object>> list = mapper.readValue(body, ArrayList.class);

            for (Map<String, Object> map : list) {
                RecentJob recentJob = new RecentJob();
                String actionStr = (String) map.get("_job_type");
                if (actionStr.equals("diffusion")) {
                    recentJob.setAction(Action.IMAGINE);
                } else if (actionStr.contains("upsample")) {
                    recentJob.setAction(Action.UPSCALE);
                } else if (actionStr.equals("variation")) {
                    recentJob.setAction(Action.VARIATION);
                }
                recentJob.setUsername((String) map.get("username"));
                recentJob.setChannelId((String) map.get("platform_channel_id"));
                recentJob.setGuildId((String) map.get("guild_id"));
                recentJob.setPrompt((String) map.get("prompt"));
                int left = recentJob.getPrompt().indexOf("[");
                int right = recentJob.getPrompt().indexOf("]");
                if (left != -1 && right != -1) {
                    recentJob.setTaskId(recentJob.getPrompt().substring(left + 1, right));
                }
                recentJob.setMessageHash((String) map.get("id"));
                recentJob.setMessageId((String) map.get("platform_message_id"));
                recentJob.setStatus((String) map.get("current_status"));
                ArrayList imageList = (ArrayList) map.get("image_paths");
                Action action = recentJob.getAction();
                if (action.equals(Action.UPSCALE)) {
                    recentJob.setImageUrl(imageList.get(0).toString());
                } else if (action.equals(Action.IMAGINE) || action.equals(Action.VARIATION)) {
                    String baseImageUrl = "https://cdn.midjourney.com/";
                    recentJob.setImageUrl(baseImageUrl + recentJob.getMessageHash() + "/grid_0.png");
                }

                if (!recentJob.getAction().equals(Action.IMAGINE)) {
                    String index = (String) map.get("reference_image_num");
                    index = index.replace(" ", "");
                    recentJob.setIndex(Integer.parseInt(index));
                }

                recentJob.setChannelId((String) map.get("platform_channel_id"));
                recentJob.setGuildId((String) map.get("guild_id"));
                recentJobs.add(recentJob);
            }
            return recentJobs;
        } catch (Exception e) {
            log.error("解析recent Job失败", e);
        }
        return null;
    }

}
