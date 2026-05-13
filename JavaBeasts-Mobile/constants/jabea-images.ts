import { ImageSourcePropType } from 'react-native';

const JABEA_IMAGES: Record<string, ImageSourcePropType> = {
  aquaryx: require('../assets/jabeas/aquaryx.png'),
  branther: require('../assets/jabeas/branther.png'),
  brawlex: require('../assets/jabeas/brawlex.png'),
  cindrel: require('../assets/jabeas/cindrel.png'),
  codrex: require('../assets/jabeas/codrex.png'),
  electryn: require('../assets/jabeas/electryn.png'),
  flamara: require('../assets/jabeas/flamara.png'),
  florion: require('../assets/jabeas/florion.png'),
  griffel: require('../assets/jabeas/griffel.png'),
  ignarok: require('../assets/jabeas/ignarok.png'),
  marvyn: require('../assets/jabeas/marvyn.png'),
  nerulon: require('../assets/jabeas/nerulon.png'),
  pyronox: require('../assets/jabeas/pyronox.png'),
  stormix: require('../assets/jabeas/stormix.png'),
  swiftor: require('../assets/jabeas/swiftor.png'),
  sylphyra: require('../assets/jabeas/sylphyra.png'),
  tiderra: require('../assets/jabeas/tiderra.png'),
  voltari: require('../assets/jabeas/voltari.png'),
  zapphir: require('../assets/jabeas/zapphir.png'),
};

const PLACEHOLDER_IMAGE = require('../assets/jabeas/placeholder.png');

export function getJaBeaImage(name?: string | null): ImageSourcePropType {
  if (!name) {
    return PLACEHOLDER_IMAGE;
  }

  return JABEA_IMAGES[normalizeJaBeaName(name)] ?? PLACEHOLDER_IMAGE;
}

function normalizeJaBeaName(name: string) {
  return name
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .toLowerCase();
}
