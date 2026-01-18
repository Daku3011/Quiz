# External Access Guide

This guide explains how to access the Quiz System from other devices (phones, tablets, laptops) on the same Wi-Fi/Local Network.

## 📋 Server Details

- **Server IP Address**: `192.168.29.115`
- **Backend Port**: `8080`
- **Faculty Portal Port**: `9876`

---

## 👨‍🏫 For Faculty (Portal)

Access the management dashboard from any browser:

- **URL**: [http://192.168.29.115:9876](http://192.168.29.115:9876)

---

## 👨‍🎓 For Students (Portal)

### Option 1: Web Portal (Recommended for Mobile/Tablets)

Access the quiz directly in your browser:

- **URL**: [http://192.168.29.115:8080](http://192.168.29.115:8080)

### Option 2: JavaFX Client (For Laptops/PCs)

To run the specialized student client and connect to the server:

1. Open a terminal/command prompt in the `client` folder.
2. Run the following command:
   ```bash
   mvn javafx:run -Dapp.url=http://192.168.29.115:8080
   ```

---

## ⚠️ Troubleshooting (Mobile/External Access)

If the pages are not opening on your mobile device, it is likely because your computer's firewall is blocking incoming connections.

### Quick Fix (Run on your Linux Computer):

Open a terminal on your computer and run these three commands to allow the quiz ports:

```bash
sudo ufw allow 8080/tcp
sudo ufw allow 9876/tcp
sudo ufw reload
```

### Other things to check:

1. **Same Wi-Fi**: Double-check that both your computer and mobile are connected to the exact same Wi-Fi network.
2. **Access Point Isolation**: Some guest/office Wi-Fi networks prevent devices from seeing each other. Try using a mobile hotspot if possible.
3. **Check IP**: Ensure your server IP is still `192.168.29.115` (run `hostname -I` to confirm).
