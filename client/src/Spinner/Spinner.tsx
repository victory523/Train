import { FC } from "react";
import cx from "clsx";
import styles from "./Spinner.module.css";

export const Spinner: FC = (props) => (
  <div {...props} className={styles.container} data-testid="spinner">
    <div className={cx(styles.bar, styles.bar1)}></div>
    <div className={cx(styles.bar, styles.bar2)}></div>
    <div className={cx(styles.bar, styles.bar3)}></div>
    <div className={cx(styles.bar, styles.bar4)}></div>
    <div className={cx(styles.bar, styles.bar5)}></div>
  </div>
);
