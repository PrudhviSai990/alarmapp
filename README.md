# ⏰ SmartAlarm

A next-gen Android alarm app that refuses to be ignored. SmartAlarm blends precision scheduling with interactive wake-up challenges — because dismissing an alarm should require brainpower, not just a thumb.

## ✨ Features

- ⏰ **Exact Alarms** — Reliable triggers using `AlarmManager` and `SCHEDULE_EXACT_ALARM`
- 🔔 **Custom Notifications** — Full-screen alerts with dismiss logic, lockscreen visibility, and sound control
- 🧠 **Gamified Dismissals** — Mini-games (tap, memory, quiz) to ensure you're actually awake
- 📱 **Modern UI** — Full-screen activity over lockscreen, adaptive to Android 12–14+
- 🔒 **Permission Flow** — Runtime checks for `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, and background launch policies

## 🛠️ Tech Stack

- Kotlin / Android SDK
- `AlarmManager`, `PendingIntent`, `BroadcastReceiver`
- `NotificationManager`, `NotificationChannel`
- `MediaPlayer`, `WindowManager`, `PowerManager`
- Android 12–14 compatibility
