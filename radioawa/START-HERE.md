# radioawa - Start Here! 🎵

The simplest guide to get radioawa streaming in under 2 minutes.

---

## 🎯 Super Quick Start

### 1️⃣ Start Everything

```bash
./start-all.sh
```

### 2️⃣ Open Your Browser

```
http://localhost:5171
```

### 3️⃣ Choose Your Station

Click **English** or **हिंदी** (Hindi) button at the top

### 4️⃣ Click Play

Click the big play button and enjoy lossless streaming! 🎵

### 5️⃣ Rate Songs

Use 👍 thumbs up or 👎 thumbs down to rate songs you love or dislike!

---

## 🛑 Stop Everything

```bash
./stop-all.sh
```

---

## 📊 Check What's Running

```bash
./check-status.sh
```

---

## 🆘 Troubleshooting

### Scripts not working?

Make sure they're executable:
```bash
chmod +x *.sh
```

### Something not starting?

1. **Check Java:**
   ```bash
   java -version  # Should be 17+
   ```

2. **Check Node:**
   ```bash
   node -v  # Should be 18+
   ```

3. **Check PostgreSQL:**
   ```bash
   brew services list | grep postgresql
   ```

### Still having issues?

See detailed guides:
- [QUICKSTART.md](./QUICKSTART.md) - Full quick start guide
- [SETUP.md](./SETUP.md) - Detailed setup instructions
- [README.md](./README.md) - Complete project documentation

---

## 📦 What Gets Started

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| **Frontend** | 5171 | http://localhost:5171 | Radio player interface |
| **Backend** | 8081 | http://localhost:8081 | API server |
| **Database** | 5432 | localhost:5432 | PostgreSQL database |

---

## 🎮 Using the Player

Once you open http://localhost:5171:

1. **Switch Stations** - Click English or हिंदी buttons at the top
2. **Play/Pause** - Click the large circular button
3. **Volume** - Use the slider (0-100%)
4. **Rate Songs** - Click 👍 or 👎 to rate the current song
5. **Status** - Watch the indicator:
   - Gray = Offline
   - Mint = Ready to Play
   - Orange = Loading/Buffering
   - Green (pulsing) = LIVE streaming! 🎵

---

## 📁 Available Scripts

| Script | What It Does |
|--------|--------------|
| `start-all.sh` | Start all services (backend, frontend, PostgreSQL) |
| `stop-all.sh` | Stop backend and frontend |
| `check-status.sh` | Check status of all services |

---

## 🌐 Stream Information

### English Station
- **Stream URL:** `https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8`
- **Quality:** 24-bit / 48 kHz Lossless
- **Format:** HLS (HTTP Live Streaming)
- **Codec:** AAC

### Hindi Station (हिंदी)
- **Stream URL:** Vividh Bharati (All India Radio)
- **Content:** Classic Hindi film music
- **Quality:** High-quality HLS stream

---

## 💡 Quick Tips

- **View Logs:**
  ```bash
  tail -f backend.log
  tail -f frontend.log
  ```

- **Restart Everything:**
  ```bash
  ./stop-all.sh && ./start-all.sh
  ```

- **First Time Setup:**
  ```bash
  # Install frontend dependencies first
  cd frontend && npm install && cd ..

  # Then start
  ./start-all.sh
  ```

---

## 🎓 Next Steps

Once you have it running:

1. ✅ Test both English and Hindi stations
2. ✅ Rate some songs and watch the counts update
3. ✅ Explore the multi-station database in PostgreSQL
4. ✅ Run the test suite: `mvn test` and `npm run test`
5. ✅ Explore the code structure in [README.md](./README.md)
6. ✅ Learn Docker deployment in [DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md)
7. ✅ Learn traditional deployment in [DEPLOYMENT.md](./DEPLOYMENT.md)

---

## 🚀 That's It!

You're now running radioawa - a professional lossless audio streaming platform!

**Remember:**
- Start: `./start-all.sh`
- Open: `http://localhost:5171`
- Stop: `./stop-all.sh`

Enjoy your music! 🎵🎧
