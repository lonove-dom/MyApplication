package com.example.note.justdo;

public class Update {
    public void update(){
        // 2018.10.25  从地图返回时修改全局变量
        // 2018.10.29  bug修改，icon制作
        // 2018.10.30  同步到github，可以实现多地办公
        // 2018.10.31  初步编写设置页面
        // 2018.11.01  初步编写widget组件
        // 2018.11.02  widget 标题图标跳转主程序,点击完成
    }

    public void bug(){
        // 设置检测有内容时候才可以点击地点和时间提醒按钮，图标变灰，不可点击
        // -----搜索后的点是与已创建的marker冲突---2018.10.25
        // 在时间设置打开的情况下按返回键会返回主界面时间设置窗口不缩回
        // -----使用自定义按钮返回位置后的返回地址有问题---2018.10.29
        // -----圆的可见度获取问题，无圆时也会弹出窗口---2018.10.29
        // -----搜索后多marker的情况下无法识别标题和位置导致空指针闪退---2018.10.29
        // 定位按钮没有边框，不好看
        // 设置页面的粉色字
        // 不等撤销删除的动画消失就无法删除事项
        // 文本框距离时间显示太近
    }

    public void todo(){
        // 在一开始添加使用说明事项*******welcome
        // -----设置界面初步实现==--2018.10.31
        // 实现上拉打开设置页面有个箭头
        // 上拉打开设置页面*******识别上拉
        // 提示获取权限
        // 修改启动动画
        // -----修改titlebar的颜色---2018.10.29
        // 区别启动页和首次打开的展示页
        // 换行后创建多条事项
        // 地点提醒服务的实现*****
        // 摇一摇清除所有已完成*****
        // -----撤销删除的icon---2018.11.02
        // -----页面小组件--2018.11.01
        // 没有任务时出现小游戏
        // 设置widget点击完成某项事件******WidgetProvider
        // 设置widget可以显示地点提醒和时间提醒
        // 设置widget美观UI（字体，颜色）
    }

    public void remember(){
        // 图标版权：<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
        // 地图版权
    }
}
