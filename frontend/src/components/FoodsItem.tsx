import React from 'react';
// import { useNavigate } from 'react-router-dom';
import { Button, Card, Grid, Icon, Image, Label, SemanticCOLORS } from 'semantic-ui-react';
import { categories } from '../Datas';
import { ISingleFoods, ResultFoods } from '../models/IFoods';
import { adminFoodDelete, adminPublishPendingFoods } from '../Services';
import { fncDateConvert } from '../Util';
import { buildStyles, CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';

interface itemType {
    item: ResultFoods,
    status?: boolean,
    isAdmin?: boolean,
}

export default function FoodsItem(props: itemType) {

    const glycemicColor = (index: number): SemanticCOLORS => {
        var color: SemanticCOLORS = 'red'
        if (index > 0 && index < 56) {
            color = 'green'
        } else if (index > 55 && index < 71) {
            color = 'orange'
        } else if (index > 70) {
            color = 'red'
        }
        return color;
    }

    // goto detail
    const fncGotoDetail = (url: string) => {
        //navigate("/details/"+url)
        window.open("/details/" + url, "_blank")
    }

    // goto push
    const fncPush = () => {
        const itm = props.item
        itm.enabled = true
        adminPublishPendingFoods(itm).then(res => {
            const dt: ISingleFoods = res.data
            if (dt.status === true) {
                window.location.href = "/waitFoodsList"
            }
        }).catch(err => {

        })
    }

    const deleteItem = () => {
        const itm = props.item
        adminFoodDelete(itm.gid!).then(res => {
            const dt: ISingleFoods = res.data
            if (dt.status === true) {
                window.location.href = "/waitFoodsList"
            }
        }).catch(err => {

        })
    }

    return (
        <Grid.Column mobile={8} tablet={8} computer={4} >
            <Card>
                {props.item.image !== "" &&
                    <Image
                        style={{ height: '200px' }}
                        src={props.item.image}
                    />
                }

                {props.item.image === "" &&
                    <Image
                        style={{ height: '200px' }}
                        src='./foods.png'
                    />
                }

                <Card.Content>
                    {props.status &&
                        <div style={{ marginTop: '-15px' }}>
                            <>
                                <Label as='a' color={props.item.enabled === true ? 'teal' : 'yellow'} ribbon>
                                    {props.item.enabled === false && <Icon name='search'></Icon>}
                                    {props.item.enabled === true ? "Yayında" : "İnceleniyor"}
                                </Label>
                            </>

                        </div>
                    }


                    <Card.Header style={{ display: 'inline-block', marginTop: '15px' }}>{props.item.name}</Card.Header>
                    <div style={{ float: 'right', width: 50, height: 50 }}>
                        {glycemicColor(props.item.glycemicIndex!) === 'green' ?
                            <CircularProgressbar styles={buildStyles({ textSize: '35px', pathColor: 'green', textColor: 'green' })} value={props.item.glycemicIndex!} text={`${props.item.glycemicIndex}`} />
                            : glycemicColor(props.item.glycemicIndex!) === 'orange' ?
                                <CircularProgressbar styles={buildStyles({ textSize: '35px', pathColor: 'orange', textColor: 'orange' })} value={props.item.glycemicIndex!} text={`${props.item.glycemicIndex}`} />
                                : <CircularProgressbar styles={buildStyles({ textSize: '35px', pathColor: 'red', textColor: 'red' })} value={props.item.glycemicIndex!} text={`${props.item.glycemicIndex}`} />
                        }
                    </div>

                </Card.Content>


                <Card.Content extra>
                    <Card.Meta>Ekleyen: {props.item.createdBy === null ? 'user@mail.com' : props.item.createdBy}</Card.Meta>
                    <Card.Meta>Eklenme Tarihi: {fncDateConvert(props.item.createdDate!)}</Card.Meta>
                    <Card.Meta>Kategori: {categories[props.item.cid!].text}</Card.Meta>
                </Card.Content>


                <Card.Content extra>
                    <div className='ui two buttons'>
                        {!props.isAdmin &&
                            <>
                                <Button basic color='blue' onClick={() => fncGotoDetail(props.item.url!)} >
                                    <Icon name='info' />Detay
                                </Button>

                                <Button basic color='green' animated='vertical'>
                                    <Button.Content visible>Ekle </Button.Content>
                                    <Button.Content hidden>
                                        <Icon name='calculator' />
                                    </Button.Content>
                                </Button>
                            </>
                        }

                        {props.isAdmin &&
                            <>
                                <Button basic color='red' onClick={() => deleteItem()}>
                                    <Icon name='delete' />Sil
                                </Button>

                                <Button basic color='green' onClick={() => fncPush()} >
                                    <Icon name='info' />Yayınla
                                </Button>
                            </>
                        }

                    </div>
                </Card.Content>
            </Card>
        </Grid.Column>
    );
}
