# hp controller

#### about
HP4191a machine manipulation and data visualisation cross-platform desktop app
- HP controller 1.0 can be found [here](https://kempelen.dai.fmph.uniba.sk/HP.zip "HP controller 1.0")

#### dev
- clone this repository into your developer folder with following command

    ```
    git clone git@github.com:TIS2020-FMFI/hp.git
    ```
- open project in your favourite IDE
- reload maven


#### hpctrl lib
- find a place within your folder structure where you will download it with

    ```
    git clone https://github.com/TIS2020-FMFI/hpctrl.git
    ```

#### deploy
- within the root of your folder run
    
    ```
    mvn clean package
    ```
- jar file will be created at /target/{app name specified in pom.xml}.jar
- replace the old jar file with the one just created
- if any changes made, config.json needs to be replaced on its own

#### preview
- **cal**
<img width="1024" alt="calibration_example" src="https://user-images.githubusercontent.com/45845462/107849561-67777400-6dfc-11eb-8c21-97c4bab8a8ef.png">

- **measurement**
<img width="1124" alt="measurement_example" src="https://user-images.githubusercontent.com/45845462/107849587-90980480-6dfc-11eb-8078-978c8c6409db.png">
