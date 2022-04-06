package com.zrzyyzt.runtimeviewer.Widgets.PlaceNameSearchWidget.Entity;

import java.util.List;

public class PoiBean {

    /**
     * landmarkcount : 0
     * searchversion : 4.3.0
     * count : 14112
     * engineversion : 20180412
     * resultType : 1
     * pois : [{"eaddress":"","ename":"Supermarket","address":"北京市朝阳区","phone":"","name":"小超市","hotPointID":"40BA60059CEEAF67","lonlat":"116.607184 39.92382"},{"eaddress":"","ename":"Pin Supermarket","address":"北苑华贸天地地下1层","phone":"","name":"品超市","hotPointID":"41882401310AA165","lonlat":"116.426608 40.045064"},{"eaddress":"","ename":"Jia Supermarket","address":"朝阳门北大街6号首创大厦B座1层","phone":"","name":"家超市","hotPointID":"10B245831BD144EB","lonlat":"116.429448 39.93036"},{"eaddress":"","ename":"Longfeng Supermarket","address":"北京市房山区","phone":"","name":"龙凤超市","hotPointID":"91DC6400CA4C3D65","lonlat":"116.07688 39.577868"},{"eaddress":"","ename":"Tiantian Supermarket","address":"北京市大兴区","phone":"","name":"天天超市","hotPointID":"919C24069575BA35","lonlat":"116.31724 39.68638"},{"eaddress":"","ename":"Xiao Supermarket","address":"北京市房山区","phone":"","name":"小小超市","hotPointID":"8094248194B8923E","lonlat":"115.980304 39.696672"},{"eaddress":"","ename":"Cuiwei Supermarket","address":"花园路2号翠微百货牡丹园店1层","phone":"","name":"翠微超市","hotPointID":"417C258023403804","lonlat":"116.3596 39.97586"},{"eaddress":"","ename":"Zhenxing Supermarket","address":"北京市房山区","phone":"","name":"振兴超市","hotPointID":"51FE4405CF908F81","lonlat":"116.20336 39.5783"},{"eaddress":"","ename":"Jincheng Supermarket","address":"北京市丰台区","phone":"","name":"金成超市","hotPointID":"C0C22401FC5EA4EA","lonlat":"116.087432 39.843912"},{"eaddress":"","ename":"Ming Tea Supermarket","address":"芍药居36","phone":"","name":"茗茶超市","hotPointID":"C0F425841BA129BB","lonlat":"116.428368 39.981868"},{"eaddress":"","ename":"Xingxing Supermarket","address":"北京市房山区","phone":"010-60352078","name":"星星超市","hotPointID":"40F421020C82795D","lonlat":"116.210696 39.703"},{"eaddress":"","ename":"Zijian Supermarket","address":"北京市丰台区","phone":"","name":"子健超市","hotPointID":"D1F825060D590E4B","lonlat":"116.14484 39.80822"},{"eaddress":"","ename":"Diandian Supermarket","address":"石楼镇石楼村石楼大街37号","phone":"","name":"点点超市","hotPointID":"80D20085BD2926FD","lonlat":"115.974552 39.647468"},{"eaddress":"","ename":"Guanghan Supermarket","address":"长兴街7号院1","phone":"","name":"广瀚超市","hotPointID":"91F06103C9936E7E","lonlat":"116.180264 39.769968"},{"eaddress":"","ename":"Zijian Supermarket","address":"长青路87号院1","phone":"","name":"子健超市","hotPointID":"80EE2106738EFC35","lonlat":"116.159896 39.800528"},{"eaddress":"","ename":"Tobacco and Liquor Supermarket","address":"北京市门头沟区","phone":"","name":"烟酒超市","hotPointID":"804625009FBA801F","lonlat":"116.103248 39.931968"},{"eaddress":"","ename":"Shunshun Supermarket","address":"北京市丰台区","phone":"","name":"顺顺超市","hotPointID":"D0140106BA3DCCE6","lonlat":"116.142792 39.77358"},{"eaddress":"","ename":"Yangzi Supermarket","address":"北京市房山区","phone":"","name":"杨子超市","hotPointID":"00D60403992251FD","lonlat":"116.142208 39.69804"},{"eaddress":"","ename":"Tobacco and Liquor Supermarket","address":"北京市海淀区","phone":"","name":"烟酒超市","hotPointID":"508E6403C95CF785","lonlat":"116.29528 39.924852"},{"eaddress":"","ename":"Tianshun Supermarket","address":"北京市房山区","phone":"010-81302186","name":"天顺超市","hotPointID":"506640839DFED5B2","lonlat":"115.964656 39.762228"}]
     * dataversion : 2019-9-25 20:12:38
     * prompt : [{"type":4,"admins":[{"name":"北京市","adminCode":156110000}]}]
     * mclayer :
     * keyWord : 超市
     */

    private String landmarkcount;
    private String searchversion;
    private String count;
    private String engineversion;
    private String resultType;
    private String dataversion;
    private String mclayer;
    private String keyWord;
    private List<PoisBean> pois;
    private List<PromptBean> prompt;

    public String getLandmarkcount() {
        return landmarkcount;
    }

    public void setLandmarkcount(String landmarkcount) {
        this.landmarkcount = landmarkcount;
    }

    public String getSearchversion() {
        return searchversion;
    }

    public void setSearchversion(String searchversion) {
        this.searchversion = searchversion;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getEngineversion() {
        return engineversion;
    }

    public void setEngineversion(String engineversion) {
        this.engineversion = engineversion;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getDataversion() {
        return dataversion;
    }

    public void setDataversion(String dataversion) {
        this.dataversion = dataversion;
    }

    public String getMclayer() {
        return mclayer;
    }

    public void setMclayer(String mclayer) {
        this.mclayer = mclayer;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<PoisBean> getPois() {
        return pois;
    }

    public void setPois(List<PoisBean> pois) {
        this.pois = pois;
    }

    public List<PromptBean> getPrompt() {
        return prompt;
    }

    public void setPrompt(List<PromptBean> prompt) {
        this.prompt = prompt;
    }

    public static class PoisBean {
        /**
         * eaddress :
         * ename : Supermarket
         * address : 北京市朝阳区
         * phone :
         * name : 小超市
         * hotPointID : 40BA60059CEEAF67
         * lonlat : 116.607184 39.92382
         */

        private String eaddress;
        private String ename;
        private String address;
        private String phone;
        private String name;
        private String hotPointID;
        private String lonlat;

        public String getEaddress() {
            return eaddress;
        }

        public void setEaddress(String eaddress) {
            this.eaddress = eaddress;
        }

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHotPointID() {
            return hotPointID;
        }

        public void setHotPointID(String hotPointID) {
            this.hotPointID = hotPointID;
        }

        public String getLonlat() {
            return lonlat;
        }

        public void setLonlat(String lonlat) {
            this.lonlat = lonlat;
        }
    }

    public static class PromptBean {
        /**
         * type : 4
         * admins : [{"name":"北京市","adminCode":156110000}]
         */

        private String type;
        private List<AdminsBean> admins;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<AdminsBean> getAdmins() {
            return admins;
        }

        public void setAdmins(List<AdminsBean> admins) {
            this.admins = admins;
        }

        public static class AdminsBean {
            /**
             * name : 北京市
             * adminCode : 156110000
             */

            private String name;
            private String adminCode;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAdminCode() {
                return adminCode;
            }

            public void setAdminCode(String adminCode) {
                this.adminCode = adminCode;
            }
        }
    }
}
