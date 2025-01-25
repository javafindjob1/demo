package com.abc;

import java.util.Arrays;

public class A {
    public static void main(String[] args) {
        int[] a = new int[] { 6, 1, 6, 2, 7, 9, 3, 4, 10, 8 };

        // 冒泡(a)
        // 快速排序(a, 0, a.length - 1);
        // 选择排序(a);
        // 堆排序(a);
        // heapSort(a);
        // 插入排序(a);
        // shell(a);
        mergeSort2(a);

        System.out.println(Arrays.toString(a));
    }

    public static void 冒泡(int[] a) {
        for (int j = 0; j < a.length - 1; j++) {
            for (int i = 0; i < a.length - j - 1; i++) {
                if (a[i] > a[i + 1]) {
                    int tmp = a[i + 1];
                    a[i + 1] = a[i];
                    a[i] = tmp;
                }
            }

            System.out.println(Arrays.toString(a));
        }
    }

    public static int partSort(int[] a, int left, int right) {
        if (left >= right) {
            return left;
        }

        int base = a[left];
        while (right > left) {
            while (right > left && a[right] >= base) {
                right--;
            }
            // 右坑填上
            a[left] = a[right];

            while (left < right && a[left] < base) {
                left++;
            }
            // 左坑填上
            a[right] = a[left];
        }
        // 相遇之后 right=left
        a[left] = base;

        System.out.println(Arrays.toString(a));
        return right;
    }

    public static void 快速排序(int[] a, int left, int right) {
        if (left >= right) {
            return;
        }

        int base = partSort(a, left, right);

        快速排序(a, left, base - 1);
        快速排序(a, base + 1, right);
    }

    public static void 选择排序(int[] a) {
        for (int j = 0; j < a.length - 1; j++) {
            int minI = j;
            for (int i = j + 1; i < a.length; i++) {
                if (a[i] < a[minI]) {
                    minI = i;
                }
            }
            if (a[minI] < a[j]) {
                int tmp = a[minI];
                a[minI] = a[j];
                a[j] = tmp;
            }
            System.out.println(Arrays.toString(a));
        }

    }

    public static void swap(int[] a, int n, int m) {
        int tmp = a[m];
        a[m] = a[n];
        a[n] = tmp;
    }

    public static void ajustUp(int[] a, int m, int len) {
        if (2 * m > len) {
            return;
        }

        while (m > 0) {
            int maxI = m - 1;
            if (a[maxI] < a[2 * m - 1]) {
                maxI = 2 * m - 1;
            }
            if (2 * m + 1 <= len) {
                if (a[maxI] < a[2 * m]) {
                    maxI = 2 * m;
                }
            }
            if (maxI != m - 1) {
                swap(a, m - 1, maxI);
                m = m / 2;
            } else {
                break;
            }
        }
    }

    public static void 堆排序(int[] a) {
        // 构建堆
        for (int m = 1; m < a.length; m++) {
            ajustUp(a, m, a.length);
        }

        System.out.println(Arrays.toString(a));
        // 堆排序
        for (int j = a.length - 1; j > 0; j--) {
            swap(a, 0, j);
            for (int m = 1; m < a.length; m++) {
                ajustUp(a, m, j);
            }
        }

    }

    public static void heapSort(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            while (2 * i + 1 < n) {
                int j = 2 * i + 1;
                if (j + 1 < n && a[j] < a[j + 1]) {
                    j++;
                }
                if (a[i] < a[j]) {
                    swap(a, i, j);
                    i = j;
                } else {
                    break;
                }
            }
        }

        System.out.println(Arrays.toString(a));

        for (int z = n - 1; z > 0; z--) {
            swap(a, 0, z);
            int i = 0;
            while (2 * i + 1 < z) {
                int j = 2 * i + 1;
                if (j + 1 < z && a[j] < a[j + 1]) {
                    j++;
                }
                if (a[i] < a[j]) {
                    swap(a, i, j);
                    i = j;
                } else {
                    break;
                }
            }
        }
    }

    public static void 插入排序(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int j = i + 1;
            while (j > 0 && a[j] < a[j - 1]) {
                swap(a, j, j - 1);
                j--;
            }
        }
    }

    public static void shell(int[] a) {
        for (int r = a.length / 2; r > 0; r /= 2) {
            for (int i = 0; i < a.length - r; i++) {
                int j = i + r;
                while (j > r - 1 && a[j] < a[j - r]) {
                    swap(a, j, j - r);
                    j -= r;
                }
            }
        }
    }

    public static void mergeSort(int[] a) {
        int[] tmpArr = new int[a.length];
        msort(a, tmpArr, 0, a.length - 1);
    }

    /* 非递归版本，归并排序 arr【0...n-1】 */
    public static void mergeSort2(int arr[]) {
        int curr_size; // 当前子数组的大小，子数组大小： 1 到 n/2
        int left_start; // 左子数组的开始下标

        int n = arr.length;
        int[] tmpArr = new int[arr.length];

        // 自底向上归并. 首先归并大小为1的数组形成大小为2的有序子数组,
        // 接着归并大小为2的数组，形成大小为4的有序子数组
        for (curr_size = 1; curr_size <= n - 1; curr_size = 2 * curr_size) {
            // 根据子数组大小调整左子数组起始下标
            for (left_start = 0; left_start < n - 1; left_start += 2 * curr_size) {
                // min函数是为了防止下标越界
                // 找出左子数组结尾下标
                int mid = Math.min(left_start + curr_size - 1, n - 1);
                // 找出右子数组结尾下标
                int right_end = Math.min(left_start + 2 * curr_size - 1, n - 1);

                // 归并子数组 arr【left_start...mid】 & arr【mid+1...right_end】 ，和递归的相同
                merge(arr, tmpArr, left_start, mid, right_end);
            }
        }
    }

    public static void msort(int[] a, int[] tmpArr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            System.out.println("msort,left:" + left + ",mid=" + mid + ",right:" + right);
            msort(a, tmpArr, left, mid);
            System.out.println("msort,right:" + left + ",mid=" + mid + ",right:" + right);
            msort(a, tmpArr, mid + 1, right);
            System.out.println("msort,merge:" + left + ",mid=" + mid + ",right:" + right);
            merge(a, tmpArr, left, mid, right);
        }
    }

    public static void merge(int[] a, int tmpArr[], int left, int mid, int right) {

        int leftHead = left;
        int rightHead = mid + 1;
        // tmp数组的对应的下表
        int pos = left;

        while (leftHead <= mid && rightHead <= right) {
            if (a[leftHead] < a[rightHead]) {
                tmpArr[pos++] = a[leftHead++];
            } else {
                tmpArr[pos++] = a[rightHead++];
            }
        }

        // 剩余部分填充
        while (leftHead <= mid) {
            tmpArr[pos++] = a[leftHead++];
        }

        while (rightHead <= right) {
            tmpArr[pos++] = a[rightHead++];
        }

        while (left <= right) {
            a[left] = tmpArr[left];
            left++;
        }
    }

}
