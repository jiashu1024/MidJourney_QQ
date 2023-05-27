package com.zjs.mj.entity;

import com.zjs.mj.enums.Action;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对mj api返回的job的封装
 */
@Data
public class RecentJob {

    /**
     * 命令的类型
     */
    private Action action;
    private String taskId;
    /**
     * 任务的id，也用来作为uv操作的源任务id(mj生成的id)
     */
    private String messageHash;
    private String prompt;
    /**
     * discord的消息id
     */
    private String messageId;
    private String status; // completed
    /**
     * action未Imagine时
     */
    private String imageUrl;
    /**
     * uv操作的源任务id
     */
    private String referenceJobId;

    /**
     * 父任务id
     */
    private String parentId;

    private String channelId;

    private String guildId;

    private String username;

    private int index;

   // private LocalDateTime enqueueTime;



    public RecentJob() {

    }


    //    {
//        "_job_type": "diffusion",
//            "_parsed_params": {
//        "creative": false,
//                "fast": false,
//                "hd": false,
//                "niji": false,
//                "no": [],
//        "quality": 2,
//                "style": "",
//                "test": false,
//                "testp": false,
//                "tile": false,
//                "upanime": false,
//                "upbeta": false,
//                "uplight": false,
//                "version": 5,
//                "vibe": false,
//                "video": false
//    },
//        "_service": "main",
//            "avatar_job_id": null,
//            "avatar_job_index": null,
//            "cover_job_id": null,
//            "cover_job_index": null,
//            "current_status": "completed",
//            "enqueue_time": "2023-05-26 07:38:42.209596",
//            "event": {
//        "height": 1024,
//                "textPrompt": [
//        "[4565437755075045]Two people running in the playground, the sun"
//			],
//        "imagePrompts": [],
//        "width": 1024,
//                "batchSize": 4,
//                "textPromptWeights": [
//        1
//			],
//        "seedImageURL": null,
//                "eventType": "imagine",
//                "test": false
//    },
//        "flagged": false,
//            "followed_by_user": false,
//            "grid_id": null,
//            "grid_num": null,
//            "guild_id": "1109353287091748978",
//            "hidden": false,
//            "id": "012e8f80-7e9b-48fe-8839-37a5b61a14aa",
//            "image_paths": [
//        "https://cdn.midjourney.com/012e8f80-7e9b-48fe-8839-37a5b61a14aa/0_0.png",
//                "https://cdn.midjourney.com/012e8f80-7e9b-48fe-8839-37a5b61a14aa/0_1.png",
//                "https://cdn.midjourney.com/012e8f80-7e9b-48fe-8839-37a5b61a14aa/0_2.png",
//                "https://cdn.midjourney.com/012e8f80-7e9b-48fe-8839-37a5b61a14aa/0_3.png"
//		],
//        "is_published": true,
//            "liked_by_user": false,
//            "low_priority": false,
//            "metered": false,
//            "mod_hidden": false,
//            "parent_grid": null,
//            "parent_id": null,
//            "platform": "discord",
//            "platform_channel": "Untracked",
//            "platform_channel_id": "1109353288333271162",
//            "platform_message_id": "1111559185956618241",
//            "platform_thread_id": null,
//            "prompt": "[4565437755075045]Two people running in the playground, the sun",
//            "ranked_by_user": false,
//            "ranking_by_user": null,
//            "type": "grid",
//            "user_actions": null,
//            "user_id": "48d7667d-4177-49e2-adc1-5674bdde9593",
//            "user_reactions": null,
//            "username": "amoreno",
//            "full_command": "[4565437755075045]Two people running in the playground, the sun     --v 5 --q 2",
//            "reference_job_id": null,
//            "reference_image_num": null
//    },
}
