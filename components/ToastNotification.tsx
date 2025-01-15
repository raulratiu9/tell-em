// ToastNotification.tsx
import React from "react";
import Toast from "react-native-toast-message";

interface ToastNotificationProps {
  type: "success" | "error" | "info";
  text1: string;
  text2?: string;
}

const ToastNotification: React.FC<ToastNotificationProps> = ({
  type,
  text1,
  text2,
}) => {
  React.useEffect(() => {
    Toast.show({
      type: type,
      text1: text1,
      text2: text2,
      position: "bottom",
    });
  }, [type, text1, text2]);

  return null;
};

export default ToastNotification;
