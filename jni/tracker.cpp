#include "edu_happy_detection_DistanceTracker.h"
#include <opencv2/opencv.hpp>
#include <string>
#include <vector>
#include"highgui.h"
#include <cxcore.h>
#include <vector>
#include <algorithm>
#include <math.h>
#include<cv.h>
#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

int grey[41][201];
int total=0,total_weight=0;
CvSize size_of_img =cvSize(400,400);
CvPoint p1[400],p2[400],p3[400],p4[400];
CvPoint final_center[100000];
int final_cener_i=0;
struct PointandNumber{
	int k;
	CvPoint point_o;
};

struct pack{
	CvPoint center;
	IplImage *img;
};

PointandNumber get_point(const vector<Vec4i>& liness,IplImage *img){
	int wid=size_of_img.width,
		hei=size_of_img.height;
	vector<Vec4i>::const_iterator i,j;
	int countt=0,count_final,dk=0;
	CvPoint point_o;
	PointandNumber ans;
	int	point_x[100000],x_ave=0,
		point_y[100000],y_ave=0,
		k=0;
		printf("detect: %d lines\n",liness.size());
	if(liness.size()==0){
		printf("no lines detected \n");
		ans.k=-233;
		ans.point_o=cvPoint(-233,-233);
		return ans;
	}
	for(i=liness.begin();i<liness.end()-1;i++){
		Point pti1((*i)[0],(*i)[1]);
		Point pti2((*i)[2],(*i)[3]);
		int		x1=pti1.x,x2=pti2.x,
				y1=pti1.y,y2=pti2.y;
		if(x1==x2)
			continue;
		double k1=(double)( (double)y1-(double)y2)/((double)x1-(double)x2);
		if(
			(k1<=0 && ((double)	k1>-0.25))
			||
			(k1>=0 && ((double)k1<0.25))
		) continue;
		for(j=i+1;j<liness.end() ;j++){
			Point ptj1((*j)[0],(*j)[1]);
			Point ptj2((*j)[2],(*j)[3]);
			int		x1b=ptj1.x,x2b=ptj2.x,
					y1b=ptj1.y,y2b=ptj2.y;
			double k2=(double)( (double)y1b-(double)y2b)/((double)x1b-(double)x2b);
			if(k1*k2>0)
				continue;
			if(x1b==x2b)
				continue;
			if(
				(k2<=0 && k2>-0.25)
				||(
					k2>=0&& k2<0.25
				   )
			)continue;
			int xI,xII,yI,yII,xt,yt;
			xI=(floor)((x1b*y2 - x2b*y2 - x1b*y2b + x2b*y1b)*x1 + x2*x2b*y1 - x2*x1b*y1 + x2*x1b*y2b - x2*x2b*y1b);
			xII=(ceil)((y1b - y2b)*x1 + x1b*y2 - x2*y1b - x1b*y1 + x2b*y1 + x2*y2b - x2b*y2);
			yI=(floor)(x1*y2*y1b - x2*y1*y1b - x1*y2*y2b + x2*y1*y2b - x1b*y1*y2b + x2b*y1*y1b + x1b*y2*y2b - x2b*y2*y1b);
			yII=(ceil)(x1*y1b - x1b*y1 - x1*y2b - x2*y1b + x1b*y2 + x2b*y1 + x2*y2b - x2b*y2);
			if(xII*yII==0) continue;
			xt=xI/xII;yt=yI/yII;
			if(xt<0||yt<0||xt>wid||yt>hei) continue;
			k++;
			p1[k]=pti1;p2[k]=pti2;
			p3[k]=ptj1;p4[k]=ptj2;
			point_x[k]=xt;
			point_y[k]=yt;
			x_ave+=point_x[k];y_ave+=point_y[k];
			//cvDrawCircle(img,cvPoint(xt,yt),3,cvScalar(255,255,255),-1);
		}
	}
	if(k==0){
		printf("no points left after selected stage1 \n");
		ans.k=-233;
		ans.point_o=cvPoint(-233,-233);
		return ans;
	}
	else{
	x_ave/=k;
	y_ave/=k;
	}
	int ii,jj;
	for(ii=1;ii<=k;ii++){
		countt=0;
		for(jj=1;jj<=k;jj++){
			if((point_x[ii]-point_x[jj])*(point_x[ii]-point_x[jj])+(point_y[ii]-point_y[jj])*(point_y[ii]-point_y[jj] )>500)
				countt++;
		}
		if(countt >=2/3*k){
			x_ave= x_ave * k - point_x[ii];
			y_ave = y_ave * k - point_y[ii];
			int temp = point_x[k];
			point_x[k] = point_x[ii];
			point_x[ii] = temp;
			temp = point_y[k];
			point_y[k] = point_y[ii];
			point_y[ii] = temp;
			k--;
			if(k==0){
			printf("no points left after selected stage2 \n");
			ans.k=-233;
			ans.point_o=cvPoint(-233,-233);
			return ans;
	}
			x_ave /= k;
			y_ave /= k;
		}
	}
	x_ave;y_ave;
	ii=1;
	while (ii<=k){
		if((point_x[ii]-x_ave)*(point_x[ii]-x_ave)+(point_y[ii]-y_ave)*(point_y[ii]-y_ave )>500){
			x_ave = x_ave *  k - point_x[ii];
			y_ave = y_ave * k - point_y[ii];
			int temp = point_x[k];
			point_x[k] = point_x[ii];
			point_x[ii] = temp;
			temp = point_y[k];
			point_y[k] = point_y[ii];
			point_y[ii] = temp;
			k--;
			if(k==0){
			printf("no points left after selected stage3 \n");
			ans.k=-233;
			ans.point_o=cvPoint(-233,-233);
			return ans;
			}
			x_ave = x_ave / k+1;
			y_ave = y_ave / k+1;
		}
		ii++;
	}

	cout<<"total points:"<<k<<endl;
	ans.point_o=cvPoint(x_ave,y_ave);
	ans.k=k;
	return ans;
}


void drawDetectLines(IplImage *img,const vector<Vec4i>& lines,Scalar &color)
{
    vector<Vec4i>::const_iterator it=lines.begin(); int k=0;
    while(it!=lines.end())
    {
        Point pt1((*it)[0],(*it)[1]);
        Point pt2((*it)[2],(*it)[3]);
        cvLine(img,pt1,pt2,color,1);
        ++it;
		k++;
    }
}

void quicksort(int left,int right,int k)
//从小到大排序，left左指针，right右指针，k表示行
{
    int i,j,t,temp;
    if(left>right)
       return;
	temp=grey[k][left];
    i=left;
    j=right;
    while(i!=j)
    {
		while(grey[k][j]>=temp && i<j)
			j--;
		while(grey[k][i]<=temp && i<j)
            i++;
		if(i<j)
		{
			t=grey[k][i];
			grey[k][i]=grey[k][j];
			grey[k][j]=t;
		}
    }
	grey[k][left]=grey[k][i];
	grey[k][i]=temp;
	quicksort(left,i-1,k);
	quicksort(i+1,right,k);
}

int get_white(IplImage *img){
	int i,j;
	int height=img->height;
	int width=img->width;
	int step=img->widthStep/sizeof(uchar);
	//int b[15][210],g[15][210],r[15][210];
	int data[401],white_aver=0;
	//IplImage *temp=img;
	int t;
	for(i=floor(height*0.7);i<height*0.7+40;i++)
	{
		t=i-floor(height*0.7);
		for(j=0;j<width;j++){
			data[j]=((uchar*)(img->imageData + img->widthStep*i))[j];
			grey[t][j]=data[j];
			//放到grey里面，后面排序
		}
		quicksort(0,width,t);
		int t_w=floor(width*0.6);
		white_aver+=grey[t][t_w];
		//排序完取其中0.8*宽度地方大小的灰度，灰度比这个值大的都是白的，比这个值小的都是黑的
	}
	white_aver/=30;
	//cvShowImage("temp",temp);
	return white_aver;
}

pack  findlines(IplImage *img,int k){
	vector<Vec4i> lines,lines2;
	pack ans;
	PointandNumber pnn;
	//C++: void HoughLinesP(InputArray image, OutputArray lines,
	//                      double rho, double theta, int threshold, double minLineLength=0, double maxLineGap=0 )

	int hp[7];
	if(k==1) {
		hp[1]=10;
		hp[2]=30;
		hp[3]=10;
	}
	//cvDilate(img,img);
	//cvShowImage("deliate",img);
	cvSobel(img,img,1,0,3);
	//cvCanny(img,img,300,50);
	//cvShowImage("canny",img);
	Mat mat(img);
	HoughLinesP(mat,lines,5,CV_PI/180*10,hp[1],hp[2],hp[3]);
	IplImage *img_lines=cvCreateImage(size_of_img,IPL_DEPTH_8U,1);
	for(int w=0;w<cvGetSize(img_lines).width;w++)
		for (int h = 0; h < cvGetSize(img_lines).height; h++)
			((uchar*)(img_lines->imageData + img_lines->widthStep*h))[w]=0;
	//drawDetectLines(img_lines,lines,Scalar(255,255,255)); s
	pnn=get_point(lines,img);
	if(!(pnn.point_o.x==-233&&pnn.point_o.y==-233)){
		final_cener_i++;
		final_center[final_cener_i].x=pnn.point_o.x*0.6+final_center[final_cener_i-1].x*0.4;
		final_center[final_cener_i].y=pnn.point_o.y*0.6+final_center[final_cener_i-1].y*0.4;//或者按照0.7、0.3分配
	}
	ans.center=final_center[final_cener_i];
	for(int k=1;k<=pnn.k;k++){
		cvLine(img_lines,p1[k],p2[k],cvScalar(255,255,255),4);
		cvLine(img_lines,p3[k],p4[k],cvScalar(255,255,255),4);
	}
	ans.img=img_lines;
	return ans;
}

int get_alfa_and_beta_X_Y(pack ans,int c,int r,int H){//c:车底边width坐标，r：车底边height坐标 H:摄像头距离地面距离；
	int cd=ans.center.x;//灭点width方向
	int rd=ans.center.y; // 灭点height方向
	if(cd==-233&&rd==-233)
		return -233;
	int c0=cvGetSize(ans.img).width/2;
	int r0=cvGetSize(ans.img).height/2;
	double fc=3.79*2.83;//  =摄像头焦距/像素宽度；
	double alfa=atan((r0-rd)/fc);
	double beta=atan((cd-c0)/fc*cos(alfa));
	int Z=(c-c0)*sin(beta)-(r-r0)*sin(alfa)*cos(beta)+cos(alfa)*cos(beta)*fc;
	Z=Z/((r-r0)*cos(alfa)+sin(alfa)*fc);
	Z*=H;
	return Z;
	/*atan()接受一个参数:
	angel=atan(slope)
	angel为一个角度的弧度值,要换算成角度，必须乘以180/PI,slope为直线的斜率,是一个数字,这个数字可以是负的无穷大到正无穷大之间的任何一个值.
	*/
}

JNIEXPORT jint JNICALL Java_edu_happy_detection_DistanceTracker_GetDistance
  (JNIEnv * env, jobject jo, jlong ptr, jint x, jint y){
    Mat *ptrRgb = (Mat *)ptr;
    Mat imageGray ;
    cvtColor ( * ptrRgb , imageGray , CV_RGBA2GRAY ) ;
    IplImage img = imageGray;
    IplImage *img_resize= cvCreateImage(size_of_img,IPL_DEPTH_8U,1);
    int white_aver=get_white(img_resize);
    pack ans;
    cvThreshold(img_resize,img_resize,white_aver,255,CV_THRESH_BINARY);
    vector<Vec4i> lines;
	for(int w=0;w<cvGetSize(img_resize).width;w++){
		for (int h=0;h< cvGetSize(img_resize).height*0.7; h++){
			if(h<cvGetSize(img_resize).height*0.6||h>cvGetSize(img_resize).height*0.9||w<cvGetSize(img_resize).width*0.1
					||w>cvGetSize(img_resize).width*0.9){
				((uchar*)(img_resize->imageData + img_resize->widthStep*h))[w]=0;
			}
		}
	}
	ans=findlines(img_resize,1);
	cvCopy(ans.img,img_resize,NULL);
	int z=get_alfa_and_beta_X_Y(ans,x,y,1);
	cvReleaseImage(&img_resize);
	return z;
}


