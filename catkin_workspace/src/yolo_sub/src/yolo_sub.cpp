
#include "ros/ros.h"
#include <darknet_ros_msgs/BoundingBoxes.h>
#include <darknet_ros_msgs/BoundingBox.h>
#include <darknet_ros_msgs/ObjectCount.h>
#include "std_msgs/String.h"
#include <image_transport/image_transport.h>
#include <sstream>
using namespace std;

#include <darknet_ros_msgs/BoundingBoxes.h>
#include <darknet_ros_msgs/BoundingBox.h>
#include <darknet_ros_msgs/ObjectCount.h>

int object = 0;
bool farAway = false;
int frameCount = 0;

class ImageConverter
{
private:
    ros::NodeHandle nh_;
    image_transport::ImageTransport it_;

    ros::Publisher yolo_info_pub;   
    ros::Subscriber yolo_count_sub;
    ros::Subscriber yolo_sub;

public:

    ImageConverter() : it_(nh_)
    {
        yolo_count_sub = nh_.subscribe("/darknet_ros/found_object", 100, &ImageConverter::msgCallbackCount, this);
        yolo_sub = nh_.subscribe("/darknet_ros/bounding_boxes", 100, &ImageConverter::msgCallback, this);

        //topic pub:
        yolo_info_pub = nh_.advertise<std_msgs::String>("/yolo_info", 5);
    }

    void msgCallbackCount(const darknet_ros_msgs::ObjectCount::ConstPtr &msg)
    {

        object = msg->count;

        //cout << "object count:" << msg->count << endl;
        //cout << "\033[2J\033[1;1H"; // clear terminal;
    }

    void msgCallback(const darknet_ros_msgs::BoundingBoxes::ConstPtr &msg)
    {
        int i = 0;
        int x = 0;
        int y = 0;

        for (i = 0; i < object; i++)
        {
            //  cout<<"Bouding Boxes (header):" << msg->header <<endl;
            //  cout<<"Bouding Boxes (image_header):" << msg->image_header <<endl;
            //  cout<<"Bouding Boxes (Class):" << msg->bounding_boxes[i].Class <<endl;
            //  cout<<"Bouding Boxes (Prob):" << msg->bounding_boxes[i].probability <<endl;
            //  cout<<"Bouding Boxes (xmin):" << msg->bounding_boxes[i].xmin <<endl;
            //  cout<<"Bouding Boxes (xmax):" << msg->bounding_boxes[i].xmax <<endl;
            //  cout<<"Bouding Boxes (ymin):" << msg->bounding_boxes[i].ymin <<endl;
            //  cout<<"Bouding Boxes (ymax):" << msg->bounding_boxes[i].ymax <<endl;

            x = msg->bounding_boxes[i].xmax - msg->bounding_boxes[i].xmin;
            y = msg->bounding_boxes[i].ymax - msg->bounding_boxes[i].ymin;
        }
        if (!farAway)
        {
            if (x < 200)
            {
                cout << "Bouding Boxes (x):" << x << endl;
                if (frameCount > 10)
                {
                    farAway = true;
                    cout << "farAway:" << farAway << endl;
                    frameCount = 0;
                    std_msgs::String msg;
                    msg.data = "person faraway";
                    yolo_info_pub.publish(msg);
                }
                frameCount++;
            }
        }
        if (farAway)
        {
            if (x >= 200)
            {
                cout << "Bouding Boxes (x):" << x << endl;
                if (frameCount > 10)
                {
                    farAway = false;
                    cout << "farAway:" << farAway << endl;
                    frameCount = 0;
                    std_msgs::String msg;
                    msg.data = "person back";
                    yolo_info_pub.publish(msg);
                }
                frameCount++;
            }
        }
        //cout << "\033[2J\033[1;1H"; // clear terminal
    }
};

int main(int argc, char **argv)
{
    ros::init(argc, argv, "yolo_info");
    ImageConverter imageconverter;
    ros::spin();
    return (0);
}